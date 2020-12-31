package com.kiss.proto.rocketmq.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 发送消息的三种方式：
 * 单向发送：producer发送消息到broker，不会等待broker返回结果
 * 同步发送：producer发送消息到broker，等待broker返回结果后发送完成
 * 异步发送：producer发送消息到broker后悔通过回调函数的形式返回发送状态
 *
 * @author zhangziyao
 * @date 2020/12/10
 */
@Slf4j
public class Producer {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        //定义 producer
        DefaultMQProducer producer = new DefaultMQProducer("quickstart_group1");
        //设置nameserver地址
        producer.setNamesrvAddr("49.232.166.207:9876");
        producer.setVipChannelEnabled(false);
        //启动producer
        producer.start();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            try {
                Message message = new Message("TopicTest", "TagA", ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                //同步发送
//                SendResult sendResult = producer.send(message);
//                log.info("返回结果：{}", sendResult);
                //单项发送
//                producer.sendOneway(message);
                //异步发送
                producer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        //消息发送成功返回状态
                        log.info("消息返回状态：{}", sendResult.getSendStatus());
                        log.info("消息内容：{}", sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        //异常
                        log.error(throwable.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("", e);
                Thread.sleep(1000);
            }
        }
        boolean await = countDownLatch.await(5, TimeUnit.SECONDS);
        producer.shutdown();
    }
}
