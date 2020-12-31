package com.kiss.spring.rabbit.listener;

import com.kiss.spring.rabbit.component.Producer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 后置处理器
 *
 * @author zhangziyao
 * @date 2020/12/26 15:01
 */
@Component
public class InitBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        Producer producer = (Producer) applicationContext.getBean("producer");
        RabbitTemplate rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
        //producer.confirm(rabbitTemplate);
        producer.ttlAndDlx(rabbitTemplate,"dlx.exchange.admin","dlx.zhang.admin");
    }
}
