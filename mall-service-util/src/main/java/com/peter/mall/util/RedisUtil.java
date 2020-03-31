package com.peter.mall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {

    private JedisPool jedisPool;

    public void initPool(String host, int port, int database){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(200);
        config.setMaxIdle(32);
        config.setMaxWaitMillis(10*1000);
        config.setBlockWhenExhausted(true);
        config.setTestOnBorrow(true);
        jedisPool = new JedisPool(config,host,port,20 * 1000);
    }

    public Jedis getJedis(){
        return  jedisPool.getResource();
    }
}
