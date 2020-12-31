package com.kiss.proto.rocketmq.batch;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量消息发送
 * 消息不能是延迟消息、事务消息等。消息大小不能超过4194304字节
 *
 * @author zhangziyao
 * @date 2020/12/29 9:32 下午
 */
@Slf4j
public class SplitBatchProducer {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        //设置消费者
        DefaultMQProducer producer = new DefaultMQProducer("quickstart_group1");
        //设置nameserver地址
        producer.setNamesrvAddr("49.232.166.207:9876");
        producer.start();
        List<Message> messages = new ArrayList<>(3000);
        for (int i = 0; i < 3000; i++) {
            messages.add(new Message("Batch", "tag", "Id:" + i, ("批量消息，消息ID为：" + i).getBytes()));
        }
        MessageCutting messageCutting = new MessageCutting(messages);
        while (messageCutting.hasNext()) {
            List<Message> messagesList = messageCutting.next();
            SendResult send = producer.send(messagesList);
            log.info("消息返回状态为：{}", send.getSendStatus());
        }
        producer.shutdown();
    }
}
