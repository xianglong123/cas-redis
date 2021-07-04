# cas-redis

# 线上redis测试网站
https://try.redis.io/

# redis命令文档网站
http://doc.redisfans.com/

# 参数配置
    # REDIS (RedisProperties)
    # Redis数据库索引（默认为0）
    spring.redis.database=0
    # Redis服务器地址
    spring.redis.host=localhost
    # Redis服务器连接端口
    spring.redis.port=6379
    # Redis服务器连接密码（默认为空）
    spring.redis.password=qpc_redis
    # 连接池最大连接数（使用负值表示没有限制）
    spring.redis.pool.max-active=8
    # 连接池最大阻塞等待时间（使用负值表示没有限制）
    spring.redis.pool.max-wait=-1
    # 连接池中的最大空闲连接
    spring.redis.pool.max-idle=8
    # 连接池中的最小空闲连接
    spring.redis.pool.min-idle=0
    # 连接超时时间（毫秒）
    spring.redis.timeout=0

## spring-session和springboot整合博客
    https://developer.aliyun.com/article/182676
    
    为什么要有spring-session，我们知道单机的时候session可以存储一些数据，这样每次用户请求我们都能使用这些数据，但是我们如果采用分布式去部署应用，那么
    session就不在一起，可能负载的时候就会拿不到数据，导致问题。那么我们怎么去解决这一困境呢？
    答：spring提供了组建，spring-session就是用来帮助我们来解决如上问题，我们知道session是存储在jvm的堆中的，我们如果将这个存储区域抽取出来进行统一管理
    的话就可以解决session不同步的问题，让jvm找session去redis或者jdbc中查询，这样就可以解决上述问题。至此spring-session的功能介绍完毕，实现的demo在这个项目
    中，请查收

## String
    
```bash
xianglong@123 ~ % redis-cli
127.0.0.1:6379> set name xianglong
OK
127.0.0.1:6379> get name
"xianglong"
127.0.0.1:6379> getrange name 0 2
"xia"
127.0.0.1:6379> getset name xl
"xianglong"
127.0.0.1:6379> get name
"xl"
127.0.0.1:6379> getbit name 2
(integer) 1
127.0.0.1:6379> getbit name 1
(integer) 1
127.0.0.1:6379> set a 1
OK
127.0.0.1:6379> incr a
(integer) 2
127.0.0.1:6379> get a
"2"
127.0.0.1:6379> incr a
(integer) 3
127.0.0.1:6379> decr a
(integer) 2
127.0.0.1:6379> get a
"2"
127.0.0.1:6379> 
```

### list
    1、基本数据类型，列表
    2、list可以当作栈、队列、阻塞队列 

### 事物
    1、开启事物【multi】
    2、命令入队【set name xl;get name】
    3、执行事物【exec】
    4、or取消事物【discard】·不关心
    不保证原子性，错误命令会报异常，但不会中断执行
```bash
127.0.0.1:6379(TX)> set k1 "xl"
QUEUED
127.0.0.1:6379(TX)> incr k1
QUEUED
127.0.0.1:6379(TX)> get k1
QUEUED
127.0.0.1:6379(TX)> exec
1) OK
2) (error) ERR value is not an integer or out of range
3) "xl"
```    
    
### 乐观锁CAS
    1、获取version
    2、更新 的时候比较version
```bash
127.0.0.1:6379> set money 100
OK
127.0.0.1:6379> set out 0
OK
127.0.0.1:6379> watch money #监控money
OK
127.0.0.1:6379> multi #事物正常结束、数据期间没有发生变动、这个时候就可以正常执行
OK
127.0.0.1:6379(TX)> DECRBY money 20
QUEUED
127.0.0.1:6379(TX)> incrby out 20
QUEUED
127.0.0.1:6379(TX)> exec
1) (integer) 80
2) (integer) 20
######## 多线程 修改指，使用watch可以当作redis的乐观锁操作
127.0.0.1:6379> watch money
OK
127.0.0.1:6379> multi
OK
127.0.0.1:6379(TX)> decrby money 20
QUEUED
127.0.0.1:6379(TX)> incrby out 20
QUEUED
127.0.0.1:6379(TX)> exec
(nil)
127.0.0.1:6379> 
########### 新线程的操作 ################
Last login: Fri Jul  2 17:57:08 on ttys001
xianglong@123 ~ % redis-cli
127.0.0.1:6379> set money 1000
OK
127.0.0.1:6379> 
```    
### springboot启动配置redis
```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
}
 ```

### rdb数据恢复
    优点：1、恢复效率高，适用数据量大
         2、对数据完整性要求不高
    缺点：1、会丢失一段时间的数据，可配置
```bash
172.16.116.139:6379> config get dir
1) "dir"
2) "/usr/local/redis/bin" # 如果这个目录存在dump.rdb文件，启动就会自动恢复数据
172.16.116.139:6379> 
```

### aof数据恢复
    优点：1、每一秒都同步，文件完整性更高
         2、每秒同步一次，可能会丢失一秒的数据
         3、从不同步，效率最高的！
         # appendfsync always
         appendfsync everysec
         # appendfsync no
         
    缺点：1、相对于数据文件来说，aof远远大于rdb，修复的速度也比rdb慢
         2、aof涉及大量io操作，执行效率比rdb慢，所以我们默认使用rdb持久化
         
         
```bash
[root@localhost bin]# cat appendonly.aof 
*2
$6
SELECT
$1
0
*3
$3
set
$4
name
$9
xianglong
*3
$3
set
$2
k1
$2
v1222
[root@localhost bin]# ./redis-check-aof --fix appendonly.aof  #数据修复，直接干掉错误的行，牛逼
0x              56: Expected \r\n, got: 3232
AOF analyzed: size=93, ok_up_to=61, diff=32
This will shrink the AOF from 93 bytes, with 32 bytes, to 61 bytes
Continue? [y/N]: y
Successfully truncated AOF
[root@localhost bin]# cat appendonly.aof 
*2
$6
SELECT
$1
0
*3
$3
set
$4
name
$9
xianglong
[root@localhost bin]# 
```
    

[redis序列化和反序列化](./src/main/java/com/cas/config/RedisConfig.java)
[swagger配置](./src/main/java/com/cas/config/Swagger2Config.java)
[redis注入自定义RedisTemplate](./src/main/java/com/cas/config/RedisConfig.java)
