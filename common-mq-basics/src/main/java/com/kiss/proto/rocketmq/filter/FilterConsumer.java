package com.kiss.proto.rocketmq.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * 消息过滤：上推过滤条件到broker，减少磁盘io
 * <p>
 * 支持sql92
 *
 * @author zhangziyao
 * @date 2020/12/30 9:13
 */
@Slf4j
public class FilterConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quickstart_group1");
        consumer.setNamesrvAddr("49.232.166.207:9876");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe("Filter",
                MessageSelector.bySql("(TAGS IS NOT NULL AND TAGS IN('A','C')) AND (zhang IS NOT NULL AND zhang BETWEEN 1 AND 4) "));
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
