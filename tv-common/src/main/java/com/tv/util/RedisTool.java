package com.tv.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;


@Component
public class RedisTool {
    @Autowired
    private JedisPool jedisPool;

    public Jedis getJedisResource(){
        return jedisPool.getResource();
    }
    public void realse(Jedis jedis){
        if(jedis != null){
            jedis.close();
        }
    }
    public static void main(String[] args) {
        setLockFix("testLock", "1");
        System.out.println(getKey("testLock"));
        System.out.println(setLockFix("testLock", "2"));
    }

    //缺陷版
    public static boolean setLock(String id) {
        Jedis jedis = new Jedis("localhost", 6379);
        System.out.println(jedis.ping());
        long count = jedis.setnx("testLock", id);
        if (count > 0) {
            System.out.println("获取锁成功");
            //设置过期时间
            //隐患  在获取锁成功到设置过期时间这段期间内如果发生错误  则会造成
            jedis.expire("testLock", 2);
            return true;
        } else {
            if (jedis.ttl("testLock") == -1) {
                jedis.del("testLock");
                //加锁-设置过期时间  再操作一次
                long flag = jedis.setnx("testLock", id);
                return flag > 0;
            }
            return false;
        }
    }

    //redis 2.612版本之后可以使用
    public static boolean setLockFix(String key, String value) {
        Jedis jedis = new Jedis("localhost", 6379);
        //3.0之前版本
//        String status = jedis.set(key, value, "NX", "PX", 1000);
        String status = jedis.set(key, value, SetParams.setParams().nx().px(1000));
        return "ok".equalsIgnoreCase(status);
    }

    public static String getKey(String key) {
        Jedis jedis = new Jedis("localhost", 6379);
        String value = jedis.get(key);
        return value;
    }
}
