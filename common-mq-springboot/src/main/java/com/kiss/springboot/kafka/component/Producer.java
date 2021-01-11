package com.kiss.springboot.kafka.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author zhangziyao
 * @date 2021/1/6
 */
@Component
@Slf4j
public class Producer {

    private static final String TOPIC = "kafka-topic";

    public void sendSync(KafkaTemplate<String, String> kafkaTemplate) {
        try {
            SendResult<String, String> result = kafkaTemplate.send(TOPIC, "hello springboot for sync!!!").get();
            log.info("消息发送成功！！！");
            log.info("offsets:{}", result.getRecordMetadata().offset());
            log.info("partition:{}", result.getRecordMetadata().partition());
            log.info("topic:{}", result.getRecordMetadata().topic());
            log.info("timestamp:{}", result.getRecordMetadata().timestamp());
            kafkaTemplate.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendAsSync(KafkaTemplate<String, String> kafkaTemplate) {
        kafkaTemplate.send(TOPIC, "hello springboot for asSync!!!").addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.info("消息发送失败：{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("消息发送成功！！！");
                log.info("offsets:{}", result.getRecordMetadata().offset());
                log.info("partition:{}", result.getRecordMetadata().partition());
                log.info("topic:{}", result.getRecordMetadata().topic());
                log.info("timestamp:{}", result.getRecordMetadata().timestamp());
            }
        });
        kafkaTemplate.flush();
    }
}
