package com.skin.wb.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skin.wb.entity.DataContent;
import com.skin.wb.utils.UserUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/12 13:51
 */

@Component
public class RedisMessageListener implements MessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        //请使用valueSerializer
        byte[] body = message.getBody();
//        byte[] channel = message.getChannel();
        String msg = (String) redisTemplate.getValueSerializer().deserialize(body);
//        String topic = (String)redisTemplate.getValueSerializer().deserialize(channel);
//        System.out.println("我是sub,监听"+topic+",我收到消息："+msg);

        System.out.println("监听到的消息为:" + message);
        JSONObject jsonObject = JSON.parseObject(msg);
        DataContent dataContent = JSON.toJavaObject(jsonObject, DataContent.class);
        Channel user = UserUtil.getUser(dataContent.getToId());
        if (user == null) {
            return;
        }
        System.out.println("找到用户的channel，即将发送信息");
        user.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));

    }

}
