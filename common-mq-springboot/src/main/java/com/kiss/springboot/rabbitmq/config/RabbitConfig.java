package com.kiss.springboot.rabbitmq.config;

import com.kiss.mq.model.contstants.QueueConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangziyao
 * @date 2020/12/25 16:04
 */
@Configuration
public class RabbitConfig {

    @Bean
    public Exchange exchange() {
        return ExchangeBuilder.topicExchange(QueueConstants.AMQ_TOPIC).durable(true).build();
    }

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QueueConstants.QUEUE_126).build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("*.126.#").noargs();
    }
}
