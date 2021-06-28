package com.cas.config;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class ObjectRedisSerializer implements RedisSerializer<Object> {


    @Override
    public byte[] serialize(Object o) throws SerializationException {
        //
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {

        }
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        return null;
    }
}
