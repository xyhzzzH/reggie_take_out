package com.itheima.test;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 使用jedis操作redis
 */
public class JedisTest {
    @Test
    public void testRedis(){
        // 1 获取连接
        Jedis jedis = new Jedis("localhost",6379);
        // 2 执行具体的操作
        jedis.set("usetname","xiaoming");
        String usetname = jedis.get("usetname");
        System.out.println(usetname);
        jedis.del("usetname");

        jedis.hset("myhash","addr","beijing");
        Set<String> keys = jedis.keys("*");
        keys.forEach(item->{
            System.out.println(item);
        });


        // 3 关闭连接
        jedis.close();
    }

}
