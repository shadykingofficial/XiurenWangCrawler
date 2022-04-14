package com.tonyqis.xiuren.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

public class RedisUtil {

    public static JedisPool pool;
    public static void init() {
        ResourceBundle bundle = ResourceBundle.getBundle("redis");
        if (bundle == null) {
            throw new IllegalArgumentException(
                    "[redis.properties] is not found!");
        }
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxIdle(Integer.valueOf(bundle.getString("redis.pool.maxIdle")));

        config.setTestOnBorrow(Boolean.valueOf(bundle.getString("redis.pool.testOnBorrow")));
        config.setTestOnReturn(Boolean.valueOf(bundle
                .getString("redis.pool.testOnReturn")));
        pool = new JedisPool(config, bundle.getString("redis.ip"), Integer.valueOf(bundle.getString("redis.port")));
    }

    public static void set(String key,String value){
        Jedis jedis =  pool.getResource();
        jedis.set(key,value);
        jedis.close();
    }


    public static void delete(String key){
        Jedis jedis =  pool.getResource();
        jedis.del(key);
        jedis.close();
    }

    public static boolean ifExist(String key){
        Jedis jedis =  pool.getResource();
        boolean flag = jedis.exists(key);
        jedis.close();
        return flag;
    }


    public static void flushAll(){
        Jedis jedis =  pool.getResource();
        jedis.flushAll();
        jedis.close();
    }

    public static void inrc(String key){
        Jedis jedis =  pool.getResource();
        jedis.incr(key);
        jedis.close();
    }

}
