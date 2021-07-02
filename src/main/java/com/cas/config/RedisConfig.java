package com.cas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author: xianglong[1391086179@qq.com]
 * @date: 19:33 2020-02-16
 * @version: V1.0
 * @review: 依赖注入 RedisTemplate 暂时失效
 */
@Configuration
public class RedisConfig {

    private RedisConnectionFactory redisConnectionFactory = null;

    @Bean(name = "RedisConnectionFactory")
    public RedisConnectionFactory initRedisConnectionFactory() {
        if(this.redisConnectionFactory != null) {
            return this.redisConnectionFactory;
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        //最大空闲数
        poolConfig.setMaxIdle(30);
        //最大连接数
        poolConfig.setMaxTotal(50);
        //最大等待毫秒数
        poolConfig.setMaxWaitMillis(2000);
        //创建 Jedis 连接工厂
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        this.redisConnectionFactory = connectionFactory;
        return redisConnectionFactory;
    }

    /**
     * 依赖注入 RedisTemplate
     * @return
     */
    @Bean(name = "cacheRedisTemplate")
    public RedisTemplate<String, Object> initRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        /**
         * 这里重新定义了编码序列化配置，可以支持对象的存储
         */
        redisTemplate.setDefaultSerializer(new ObjectRedisSerializer());
        redisTemplate.setKeySerializer(new ObjectRedisSerializer());
        redisTemplate.setValueSerializer(new ObjectRedisSerializer());
        redisTemplate.setConnectionFactory(initRedisConnectionFactory());
        return redisTemplate;
    }


}
