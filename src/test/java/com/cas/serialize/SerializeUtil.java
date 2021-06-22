package com.cas.serialize;

import com.cas.Base;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {

    public static void main(String[] args) {
//        test();
        test1();

    }

    /**
     * 序列化和反序列化测试
     */
    private static void test1() {
        Students stu = new Students("xainglong", 24);
        byte[] serialize = serialize(stu);
        Students deStu = (Students)deserialize(serialize);
        System.out.println(deStu);

    }

    private static void test() {
        JedisPool p = Base.startJedis();
        Jedis jedis = p.getResource();
        jedis.set("name2", "20");
        System.out.println(jedis.get("name2"));
        Base.stopJedis(p, jedis);
    }


    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            //反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


}
