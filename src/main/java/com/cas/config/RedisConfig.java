package com.cas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;

/**
 * @author: xianglong[1391086179@qq.com]
 * @date: 19:33 2020-02-16
 * @version: V1.0
 * @review: 依赖注入 RedisTemplate 暂时失效
 * springboot 2.0.X 之后redis集成底层用的 Lettuce
 */
//@Configuration
public class RedisConfig {

    private RedisConnectionFactory redisConnectionFactory = null;

    @Bean(name = "RedisConnectionFactory")
    public RedisConnectionFactory initRedisConnectionFactory() {
        if(this.redisConnectionFactory != null) {
            return this.redisConnectionFactory;
        }

        //redis配置
        RedisStandaloneConfiguration redisConfiguration = new
                RedisStandaloneConfiguration("127.0.0.1",6379);
        redisConfiguration.setDatabase(0);

        //连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig =
                new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(8);
        genericObjectPoolConfig.setMinIdle(0);
        genericObjectPoolConfig.setMaxTotal(8);
        genericObjectPoolConfig.setMaxWaitMillis(2000);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder=
                LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(2000));
        builder.poolConfig(genericObjectPoolConfig);
        builder.shutdownTimeout(Duration.ofMillis(2000));
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = builder.build();

        //创建 Jedis 连接工厂
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettucePoolingClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        this.redisConnectionFactory = lettuceConnectionFactory;
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
