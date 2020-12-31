package com.kiss.proto.rocketmq.delay;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 延迟消息
 * 开源版延迟消息分为18个等级：messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 *
 * @author zhangziyao
 * @date 2020/12/29 9:14 下午
 */
@Slf4j
public class DelayProducer {


    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        //设置消费者
        DefaultMQProducer producer = new DefaultMQProducer("quickstart_group1");
        //设置nameserver地址
        producer.setNamesrvAddr("49.232.166.207:9876");
        producer.start();
        Message message = new Message("Delay", "tag", "延迟消息！！！".getBytes());
        //messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        message.setDelayTimeLevel(3);
        SendResult send = producer.send(message);
        log.info("消息返回状态：{}", send);
        producer.shutdown();
    }

}
