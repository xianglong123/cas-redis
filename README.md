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

## 指令
    
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

[redis序列化和反序列化](./src/main/java/com/cas/config/RedisConfig.java)
[swagger配置](./src/main/java/com/cas/config/Swagger2Config.java)
[redis注入自定义RedisTemplate](./src/main/java/com/cas/config/RedisConfig.java)
