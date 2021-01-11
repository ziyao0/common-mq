package com.kiss.springboot.rabbitmq.component;

import com.kiss.mq.model.contstants.QueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangziyao
 * @date 2020/12/24 16:24
 */
@Slf4j
@Component
public class Producer {


    /**
     * 广播
     */
    public void sendFanout(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.convertAndSend(QueueConstants.AMQ_FANOUT, "", "hello fanout!!!");
    }


    /**
     * 路由模式
     */
    public void sendDirect(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.convertAndSend(QueueConstants.AMQ_DIRECT, "beijing.126.zhang.ziyao", "hello routing!!!");

    }

    /**
     * 通配符
     */
    public void sendTopic(RabbitTemplate rabbitTemplate) {
        rabbitTemplate.convertAndSend(QueueConstants.AMQ_TOPIC, "beijing.126.zhang.ziyao", "hello topic!!!");
    }


    /**
     * 通配符
     */
    public void confirmAndReturn(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 消息确认机制
             *
             * @param correlationData 返回数据
             * @param ack             消息ack，true为成功发送到exchange
             * @param cause           失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                log.info("confirm方法执行了......");
                if (ack) {
                    log.info("消息成功发送到exchange......");
                } else {
                    log.info("消息ack失败，失败内容：{}", cause);
                }
            }
        });

        rabbitTemplate.setReturnsCallback(returned -> {
            log.info("return 执行了....");
            log.info("message:{}", returned.getMessage());
            log.info("replyCode:{}", returned.getReplyCode());
            log.info("replyText:{}", returned.getReplyText());
            log.info("exchange:{}", returned.getExchange());
            log.info("routingKey:{}", returned.getRoutingKey());
        });

        Exchange exchange = new FanoutExchange("zhangziyao", true, false);
        Queue queue = new Queue("zhangziyao", true);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(new Binding("zhangziyao", Binding.DestinationType.QUEUE, "zhangziyao", "", null));
        rabbitTemplate.convertAndSend("zhangziyao", "", "hello confirm & return!!!");
    }
}
