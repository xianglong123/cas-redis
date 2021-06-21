package com.cas.test;

import com.cas.Base;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class EmbStr {

    public static void main(String[] args) {
        JedisPool p = Base.startJedis();
        Jedis jedis = p.getResource();
        jedis.set("name2", "20");
        System.out.println(jedis.get("name2"));
        Base.stopJedis(p, jedis);
    }

}
