package com.cas.serialize;

import com.cas.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

public class HashTest {
    private static final Logger log = LoggerFactory.getLogger(HashTest.class);

    public static void main(String[] args) {
        JedisPool p = Base.startJedis();
        Jedis jedis = p.getResource();
        log.info("连接成功");
//        test(jedis);
//        test1(jedis);
        base(jedis);


        Base.stopJedis(p, jedis);
    }

    public static void base(Jedis jedis) {

        String key = "person";
        jedis.hset(key, new HashMap<String, String>() {{
            put("name", "xl");
            put("age", "25");
        }});

        log.warn("指令【hincrBy】: 为哈希表 key 中的指定字段的整数值加上增量 increment ");
        System.out.println(jedis.hincrBy(key, "age", 25));
        log.warn("指令【hlen】: 获取哈希表中字段的数量");
        System.out.println(jedis.hlen(key));
        log.warn("指令【hmget】: 获取所有给定字段的值");
        jedis.hmget(key, "name", "age").forEach(System.out::println);
        log.warn("指令【】");

    }



    public static void test1(Jedis jedis) {
        log.warn("指令【hget】");
        jedis.hset("person", new HashMap<String, String>() {{
            put("name", "xl");
            put("age", "25");
        }});

        String name = jedis.hget("person", "name");
        System.out.println(String.format("获取属性[name] = %s", name));

        Map<String, String> person = jedis.hgetAll("person");
        printMap(person);

    }

    private static void printMap(Map<String, String> map) {
        log.warn("指令【hgetall】");
        for (String key : map.keySet()) {
            System.out.println(String.format("Map 属性[%s] = %s", key, map.get(key)));
        }

    }

    /**
     * 测试对象序列化
     * @param jedis
     */
    public static void test(Jedis jedis) {
        Students students = new Students();
        students.setAge(21);
        students.setName("xianglong");

        byte[] serialize = SerializeUtil.serialize(students);
        jedis.set("student".getBytes(), serialize);
        byte[] result = jedis.get("student".getBytes());
        Students obj =(Students) SerializeUtil.deserialize(result);
        assert obj != null;
        System.out.println("student: " + obj.getName() + "  " +obj.getAge());
    }

}
