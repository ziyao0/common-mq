package com.kiss.proto.rocketmq.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Consumer 消费方式有两种：
 * 推模式：由broker主动推送消息到consumer
 * 拉模式：有consumer主动找broker拉取消息
 *
 * @author zhangziyao
 * @date 2020/12/10
 */
@Slf4j
public class Consumer {
    private static Map<MessageQueue, Long> offsetMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws MQClientException {

        //推送模式
        push();
        //拉去模式
        pull();//过时 自己管理偏移量
        litePull(); //系统管理偏移量
        litePullAssign(); //自己管理偏移量

    }

    /**
     * 推送模式
     *
     * @throws MQClientException 异常
     */
    private static void push() throws MQClientException {
        //设置消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quickstart_group1");
        //设置nameserver地址
        consumer.setNamesrvAddr("49.232.166.207:9876");
        consumer.setVipChannelEnabled(false);
        //设置消费偏移量
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //订阅主题
        consumer.subscribe("TopicTest", "*");
        //设置消费时间戳
        //consumer.setConsumeTimestamp("");
        //推模式：设置监听器
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, context) -> {

            log.info("消费的消息为：{}", messages);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        log.info("consumer is stating!!!");
    }

    /**
     * 原始拉取模式
     * 1、自己管理偏移量
     *
     * @throws MQClientException 异常
     */
    public static void pull() throws MQClientException {

        //设置消费者
        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("quickstart_group1");
        //设置nameserver地址
        consumer.setNamesrvAddr("49.232.166.207:9876");
        consumer.start();
        log.info("consumer is stating!!!");
        Set<MessageQueue> messageQueueSet = consumer.fetchSubscribeMessageQueues("TopicTest");

        for (MessageQueue messageQueue : messageQueueSet) {
            SINGLE_MQ:
            while (true) {
                try {
                    PullResult pullResult = consumer.pullBlockIfNotFound(messageQueue, null, getOffset(messageQueue), 30);

                    //存储偏移量
                    putOffset(messageQueue, pullResult.getNextBeginOffset());
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                            break SINGLE_MQ;
                        case OFFSET_ILLEGAL:
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 轻量拉取，有系统管理偏移量
     *
     * @throws MQClientException 异常
     */
    public static void litePull() throws MQClientException {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("quickstart_group1");
        //设置nameserver地址
        consumer.setNamesrvAddr("49.232.166.207:9876");
        //设置从最后一次偏移量开始
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //订阅主题
        consumer.subscribe("TopicTest", "*");
        consumer.start();
        log.info("consumer is stating!!!");

        try {
            while (true) {
                List<MessageExt> messageExts = consumer.poll();
                log.info("消息内容为:{}", messageExts);
            }
        } finally {
            consumer.shutdown();
        }
    }

    /**
     * 拉模式：
     * 自己管理偏移量
     *
     * @throws MQClientException 异常
     */
    public static void litePullAssign() throws MQClientException {
        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("quickstart_group1");
        //设置nameserver地址
        consumer.setNamesrvAddr("49.232.166.207:9876");
        //取消自动提交
        consumer.setAutoCommit(false);
        consumer.start();
        log.info("consumer is stating!!!");

        Collection<MessageQueue> messageQueues = consumer.fetchMessageQueues("TopicTest");
        List<MessageQueue> messageQueueList = new ArrayList<>(messageQueues);
        List<MessageQueue> assignList = new ArrayList<>();
        for (int i = 0; i < (messageQueueList.size() / 2); i++) {
            assignList.add(messageQueueList.get(i));
        }
        consumer.assign(assignList);
        consumer.seek(assignList.get(0), 10);
        try {
            while (true) {
                List<MessageExt> extList = consumer.poll();
                log.info("消息内容为：{}", extList);
            }
        } finally {
            consumer.shutdown();
        }
    }

    private static long getOffset(MessageQueue key) {
        Long value = offsetMap.get(key);
        if (value != null) {
            return value;
        }
        return 0;
    }

    private static void putOffset(MessageQueue key, long value) {
        offsetMap.put(key, value);
    }
}
