package com.kiss.proto.rocketmq.radio;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangziyao
 * @date 2020/12/29 8:52 下午
 */
@Slf4j
public class RadioProducer {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException {

        DefaultMQProducer producer = new DefaultMQProducer("quickstart_group1");

        producer.setNamesrvAddr("49.232.166.207:9876");

        producer.start();

        CountDownLatch countDownLatch = new CountDownLatch(2);
        for (int i = 0; i < 2; i++) {
            Message message = new Message("TopicZhang", "tarA", "this is radio！！！".getBytes());
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("消息状态：{}", sendResult.getSendStatus());
                }

                @Override
                public void onException(Throwable throwable) {
                    log.error(throwable.getMessage());
                }
            });
        }
        boolean await = countDownLatch.await(5, TimeUnit.SECONDS);
        producer.shutdown();
    }
}
