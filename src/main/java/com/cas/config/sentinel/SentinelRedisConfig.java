package com.cas.config.sentinel;

import com.cas.config.ObjectRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiang_long
 * @version 1.0
 * @date 2021/7/5 10:58 上午
 * @desc 哨兵模式的配置
 *
 * TC:暂时配置会报连接异常，但是找不到原因，哨兵集群在我的本地虚拟机上面，不知道什么原因，也许是我配置有问题，也许是早上没吃饭的问题。
 */
//@EnableAutoConfiguration
//@Configuration
public class SentinelRedisConfig {
    private static Logger logger = LoggerFactory.getLogger(SentinelRedisConfig.class);

    @Value("#{'${spring.redis.sentinel.nodes}'.split(',')}")
    private List<String> nodes;

    @Bean
    public LettuceClientConfiguration getRedisConfig(){
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(8);
        genericObjectPoolConfig.setMinIdle(2);
        genericObjectPoolConfig.setMaxTotal(8);
//        genericObjectPoolConfig.setMaxWaitMillis(2000);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder=
                LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(2000));
        builder.poolConfig(genericObjectPoolConfig);
        builder.shutdownTimeout(Duration.ofMillis(2000));
        return builder.build();
    }

    @Bean
    public RedisSentinelConfiguration sentinelConfiguration() {
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        redisSentinelConfiguration.master("myredis");
        redisSentinelConfiguration.setDatabase(0);
        redisSentinelConfiguration.setPassword("123456");

        //配置redis的哨兵sentinel
        Set<RedisNode> redisNodeSet = new HashSet<>();
        nodes.forEach(x -> {
            redisNodeSet.add(new RedisNode(x.split(":")[0], Integer.parseInt(x.split(":")[1])));
        });
        logger.info("redisNodeSet -->" + redisNodeSet);
        redisSentinelConfiguration.setSentinels(redisNodeSet);
        return redisSentinelConfiguration;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisSentinelConfiguration redisConfiguration, LettuceClientConfiguration lettuceClientConfiguration) {
        return new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
    }

    @Bean(name = "cacheRedisTemplate")
    public RedisTemplate<String, Object> initRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        /**
         * 这里重新定义了编码序列化配置，可以支持对象的存储
         */
        redisTemplate.setDefaultSerializer(new ObjectRedisSerializer());
        redisTemplate.setKeySerializer(new ObjectRedisSerializer());
        redisTemplate.setValueSerializer(new ObjectRedisSerializer());
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        return redisTemplate;
    }


}
