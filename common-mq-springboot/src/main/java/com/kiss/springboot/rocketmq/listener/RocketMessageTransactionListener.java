package com.kiss.springboot.rocketmq.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author zhangziyao
 * @date 2020/12/30 15:26
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "transaction_consumer", topic = "TransactionTopic", consumeMode = ConsumeMode.CONCURRENTLY)
public class RocketMessageTransactionListener implements RocketMQListener<Object> {

    @Override
    public void onMessage(Object message) {
        log.info("事务消息内容为：{}", message);
    }
}
