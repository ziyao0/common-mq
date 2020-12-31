package com.kiss.proto.rocketmq.radio;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * 广播消费
 * {@link MessageModel#BROADCASTING}广播消费
 * {@link MessageModel#CLUSTERING}集群消费 「默认」
 *
 * @author zhangziyao
 * @date 2020/12/29 9:00 下午
 */
@Slf4j
public class RadioConsumer {

    public static void main(String[] args) throws MQClientException {

        //设置消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quickstart_group1");
        //设置nameserver地址
        consumer.setNamesrvAddr("49.232.166.207:9876");
        consumer.setMessageModel(MessageModel.BROADCASTING);
        consumer.subscribe("TopicZhang", "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            for (MessageExt messageExt : list) {
                log.info("接受到的消息为：{}", new String(messageExt.getBody()));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("consumer is stating!!!");
    }

}
