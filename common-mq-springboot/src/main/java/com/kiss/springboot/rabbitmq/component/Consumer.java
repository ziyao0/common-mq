package com.kiss.springboot.rabbitmq.component;

import com.kiss.mq.model.contstants.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

///**
// * @author zhangziyao
// * @date 2020/12/25 16:16
// */
//@Slf4j
//@Component
//public class Consumer {
//
//    @RabbitListener(queues ="zhangziyao")
//    public void test(Message message) {
//        log.info("接受到的消息内容为：{}", new String(message.getBody()));
//    }
//
//}
