package com.cas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Base {
    private static final Logger log = LoggerFactory.getLogger(Base.class);

    public static JedisPool startJedis() {
        JedisPoolConfig pool = new JedisPoolConfig();
        pool.setMaxTotal(100); // 设置最大连接数
        pool.setMaxIdle(10); // 最大空闲连接数
        // 实例化连接池
        return new JedisPool(pool, "172.16.116.139", 6381);
    }


    public static void stopJedis(JedisPool p, Jedis jedis) {
        try {
            log.warn("stop Jedis 安全退出");
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            if (p != null) { // 关闭连接池
                p.close();
            }
        }
    }

}
