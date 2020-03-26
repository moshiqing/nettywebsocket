package com.skin.wb.utils;

import io.netty.channel.Channel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/12 10:39
 */
@Component
public class UserUtil {

    RedisTemplate redisTemplate = BeanUtil.getBean(RedisTemplate.class);

    private static Map<String,Channel> map= new HashMap();

    public static void setUser(String userId, Channel channel){
        map.put(userId,channel);
    }

    public static Channel getUser(String userId){
        Channel channel = map.get(userId);
        return channel;
    }

    public static Object removeUser(String userId){
        return map.remove(userId);
    }


}
