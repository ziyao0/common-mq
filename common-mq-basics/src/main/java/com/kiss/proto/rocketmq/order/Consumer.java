package com.kiss.proto.rocketmq.order;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 顺序消费
 *
 * @author zhangziyao
 * @date 2020/12/11
 */
@Slf4j
public class Consumer {

    public static void main(String[] args) {
        DefaultMQPushConsumer consumer = null;
        try {
            consumer = new DefaultMQPushConsumer("quickstart_group1");
            consumer.setNamesrvAddr("49.232.166.207:9876");
            //消费偏移量 从最后一次进行消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.subscribe("orderTopic", "*");
            consumer.registerMessageListener((MessageListenerOrderly) (list, consumeOrderlyContext) -> {
                consumeOrderlyContext.setAutoCommit(true);
                for (MessageExt messageExt : list) {
                    log.info("本次消费消息为：{}", new String(messageExt.getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            });
            consumer.start();
            log.info("consumer is stating!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
