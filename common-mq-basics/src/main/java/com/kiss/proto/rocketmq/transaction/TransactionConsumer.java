package com.kiss.proto.rocketmq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author zhangziyao
 * @date 2020/12/30 9:13
 */
@Slf4j
public class TransactionConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("transaction");
        consumer.setNamesrvAddr("49.232.166.207:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe("transactionTopic", "*");

        consumer.registerMessageListener((MessageListenerOrderly) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                log.info("消息标签为：{}----消息内容为：{}", msg.getTags(), new String(msg.getBody()));
            }
            return ConsumeOrderlyStatus.SUCCESS;
        });
        consumer.start();
        log.info("consumer is stating!!!");
    }
}
