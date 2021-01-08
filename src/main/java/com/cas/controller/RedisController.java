package com.cas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: xianglong[1391086179@qq.com]
 * @date: 下午7:08 2021/1/8
 * @version: V1.0
 * @review:
 */
@RestController
@RequestMapping("/test")
public class RedisController {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 测试redis
     */
    @PostMapping("/redis")
    @ResponseBody
    public String redis(String num) {
        redisTemplate.opsForValue().set("key" + num, "value" + num);
        return "ok";
    }

    /**
     * 测试redis
     * redis事物 解决setIfAbsent和expire非原子操作概率bug
     *
     */
    @PostMapping("/redisSetIfAbsent")
    @ResponseBody
    public String redisSetIfAbsent() {
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.multi();
        redisTemplate.opsForValue().setIfAbsent("INCOMPATIBILITY_JOB_QUERY_STATUS_RONGLIAN","");
        redisTemplate.expire("INCOMPATIBILITY_JOB_QUERY_STATUS_RONGLIAN",15, TimeUnit.SECONDS);
        List<Object> result = redisTemplate.exec(); // 这里result会返回事务内每一个操作的结果，如果setIfAbsent操作失败后，result[0]会为false。
        System.out.println(result.get(0));
        return "ok";
    }

    /**
     * 测试redis事务，结合断点测试
     * redis事务和关系型数据库事务不一样，对于出错的命令redis只是报出错误，而错误后面的命令依旧被特别注意的地方。所以在执行redis事务前，严格地检测数据，以避免这样的事情发生
     *
     * @param num
     * @return
     */
    @PostMapping("/multi")
    @ResponseBody
    public String multi(String num) {
        redisTemplate.opsForValue().set("key1", "value" + num);
        // 开启事务要手动设置下面这个开启事务
        redisTemplate.setEnableTransactionSupport(true);
        List list = (List) new SessionCallback() {
            @Override
            public Object execute(RedisOperations ro) throws DataAccessException {
                // 设置要监控key
                ro.watch("key1");
                // 开启事务，在exec命令执行前，全部都只是进入队列
                ro.multi();
                ro.opsForValue().set("key2", "value2");
//                ro.opsForValue().increment("key" + num, 1); // 1
                // 获取值将为null,因为redis只是把命令放入队列
                Object value2 = ro.opsForValue().get("key2");
                System.out.println("命令在队列，所有 value 为null 【" + value2 + "】");
                ro.opsForValue().set("key3", "value3");
                Object value3 = ro.opsForValue().get("key3");
                System.out.println("命令在队列，所有 value 为null 【" + value3 + "】");
                // 执行 exec 命令，将先判别 key1 是否在监控后被修改过，如果是则不执行事务，否则就执行事务
                return ro.exec();
            }
        }.execute(redisTemplate);
        System.out.println(list);
        return "ok";
    }


    /**
     * 测试redis使用流水线功能
     * 10万数据读写378毫秒，一秒奖近30万数据读写
     * 与事务一样，使用流水线的过程中，所有的命令也只是进入队列而没有执行，所以所有的命令返回值都为空
     *
     * @return
     */
    @PostMapping("/pipeline")
    @ResponseBody
    public String pipeline() {
        Long start = Instant.now().toEpochMilli();
        redisTemplate.executePipelined(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                for (int i = 1; i <= 100000; i++) {
                    operations.opsForValue().set("pipeline_" + i, "value_" + i);
                    String value = (String) operations.opsForValue().get("pipeline_" + i);
                    if (i == 100000) {
                        System.out.println("命令只是进入队列， 所有值为空【" + value + "】");
                    }
                }
                return null;
            }
        });
        Long end = Instant.now().toEpochMilli();
        System.out.println("耗时：" + (end - start) + "毫秒。");
        return "ok";
    }

    /**
     * 测试 Redis 消息发送
     */
    @PostMapping("/publish")
    @ResponseBody
    public String publish() {
        redisTemplate.convertAndSend("topicl", "发布订阅测试信息");
        return "ok";
    }

    /**
     * 测试简单的Lua脚本
     */
    @PostMapping("/lua")
    @ResponseBody
    public String lua() {
        DefaultRedisScript<String> rs = new DefaultRedisScript<>();
        // 设置脚本
        rs.setScriptText("return 'Hello Redis'");
        // 定义返回类型。 注意：如果没有这个定义，Spring不会返回结果
        rs.setResultType(String.class);
        RedisSerializer<String> stringRedisSerializer = redisTemplate.getStringSerializer();
        // 执行Lua脚本
        String str = (String) redisTemplate.execute(rs, stringRedisSerializer, stringRedisSerializer, null);
        return str;
    }


}
