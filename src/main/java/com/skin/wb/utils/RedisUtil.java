package com.skin.wb.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @description: Redis锁单机版
 * @author: moshiqing
 * @time: 2020/3/26 11:47
 */
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 锁的前缀
     */
    private static String LOCK_PREFIX = "prefix";

    /**
     * 持有锁的时间
     */
    private static Long LOCK_EXPIRE = 0L;

    public Boolean lock(String key){


        String lock =LOCK_PREFIX+key;

        return (Boolean) redisTemplate.execute((RedisCallback)connection -> {

            //期望的锁的时间有效性
            long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;
            //设置到到redis setnx中
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
            if (acquire) {
                //设置成功 代表拿锁 成功
                return true;
            } else {
                //不成功就获得上锁的时间值
                byte[] value = connection.get(lock.getBytes());

                if (Objects.nonNull(value) && value.length > 0) {

                    long expireTime = Long.parseLong(new String(value));
                    //锁的时间过期
                    if (expireTime < System.currentTimeMillis()) {
                        //返回已经过期的时间戳 ,并且设置值为最新的时间
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());

                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }
}
