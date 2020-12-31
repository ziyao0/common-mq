package com.kiss.springboot.rocketmq.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author zhangziyao
 * @date 2020/12/30 14:42
 */
@Slf4j
@Component
public class Producer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void send() {
        rocketMQTemplate.convertAndSend("quick_start", "我是 springboot ！！！");
    }

    public void SendTransaction() throws InterruptedException {
        String[] tags = {"A", "B", "C"};
        for (int i = 0; i < 6; i++) {
            Message<String> message = MessageBuilder.withPayload("事务消息")
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, "TransID_" + i)
                    //发到事务监听器里后，这个自己设定的TAGS属性会丢失。但是上面那个属性不会丢失。
                    .setHeader(RocketMQHeaders.TAGS, tags[i % tags.length])
                    //MyProp在事务监听器里也能拿到，为什么就单单这个RocketMQHeaders.TAGS拿不到？这只能去调源码了。
                    .setHeader("MyProp", "MyProp_" + i)
                    .build();
            String destination = "TransactionTopic:" + tags[i % tags.length];
            TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(destination, message, destination);
            log.info("发送状态：{}", result.getSendStatus());
            Thread.sleep(20);
        }
    }
}
