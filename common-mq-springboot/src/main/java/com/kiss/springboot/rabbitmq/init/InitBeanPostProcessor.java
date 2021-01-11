package com.kiss.springboot.rabbitmq.init;

import com.kiss.springboot.rabbitmq.component.Producer;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author zhangziyao
 * @date 2021/1/1 11:03 下午
 */
@Component
public class InitBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();

        RabbitTemplate rabbitTemplate = (RabbitTemplate) context.getBean("rabbitTemplate");
        RabbitAdmin rabbitAdmin = (RabbitAdmin) context.getBean("rabbitAdmin");
        Producer producer = (Producer) context.getBean("producer");
        producer.confirmAndReturn(rabbitTemplate, rabbitAdmin);
    }
}
