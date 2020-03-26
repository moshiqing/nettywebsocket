package com.skin.wb.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.skin.wb.entity.DataContent;
import com.skin.wb.utils.UserUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/3 16:56
 */
//@Component
//@Slf4j
//public class KafkaConSuMer {
//
//    @KafkaListener(topics = {"test"})
//    public void listen(String message){
//        log.info("收到的消息"+message);
//        JSONObject jsonObject = JSON.parseObject(message);
//        DataContent dataContent = JSON.toJavaObject(jsonObject,DataContent.class);
//        Channel user = UserUtil.getUser(dataContent.getToId());
//        if(user==null){
//            return;
//        }
//        System.out.println("找到用户的channel，即将发送信息");
//        user.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
//    }
//}
