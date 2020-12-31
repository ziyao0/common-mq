package com.kiss.spring.rabbit.component;

import com.kiss.mq.model.contstants.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 *
 * @author zhangziyao
 * @date 2020/12/25 15:05
 */
@Component
@Slf4j
public class Producer {


    /**
     * 简单模式
     *
     * @param rabbitTemplate {@link RabbitTemplate}
     */
    public void queue(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.convertAndSend("queue", "hello queue!!!");
    }

    /**
     * rabbitmq 确认机制 confirm&return
     *
     * @param rabbitTemplate {@link RabbitTemplate}
     */
    public void confirm(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.setConfirmCallback((confirm, ack, res) -> {
            log.info("confirm 方法被执行！！！");
            if (ack) {
                log.info("消息已经到达交换机：{}", res);
            } else {
                log.error("消息没有到达交换机：{}，返回信息为：{}", confirm, res);
            }
        });

        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("return 执行了....");
            log.info("message:{}", message);
            log.info("replyCode:{}", replyCode);
            log.info("replyText:{}", replyText);
            log.info("exchange:{}", exchange);
            log.info("routingKey:{}", routingKey);
        });
        rabbitTemplate.convertAndSend(QueueConstants.AMQ_TOPIC, "email.qq.gaming", "hello queue!!!");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ttlAndDlx(RabbitTemplate rabbitTemplate, String exchangeName, String routingKey) {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, "我是张子尧写的死信队列！！！");
        }
    }
}
