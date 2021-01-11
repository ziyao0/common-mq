package com.kiss.springboot.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author zhangziyao
 * @date 2021/1/6
 */
@Component
@Slf4j
public class KafkaMessageListener {

    @KafkaListener(groupId = "defaultGroup01", topics = "kafka-topic")
    public void listenDefaultGroup(ConsumerRecord<String, String> record, Acknowledgment ack) {

        log.info("消息内容为：{}", record);
        log.info("------------------------------------------------------");
        ack.acknowledge();
    }

}
