package com.skin.wb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * @description:
 * @author: moshiqing
 * @time: 2020/3/17 11:23
 */
@Component
@Slf4j
public class KafkaMessageSend {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 向指定主题发送信息
     * @param topic 以服务器的ip作为主题
     * @param json 发送的消息体
     */
    public void sendMessage(String topic,String json){
        ListenableFuture future = kafkaTemplate.send(topic, json);
        future.addCallback(o -> log.info("send-消息发送成功：" + json), throwable -> log.info("消息发送失败：" + json));
    }
}
