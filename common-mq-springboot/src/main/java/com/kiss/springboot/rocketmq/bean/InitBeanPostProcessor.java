package com.kiss.springboot.rocketmq.bean;

import com.kiss.springboot.rocketmq.component.Producer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author zhangziyao
 * @date 2020/12/30 16:33
 */
@Component
public class InitBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

        RocketMQTemplate rocketMQTemplate = (RocketMQTemplate) applicationContext.getBean("rocketMQTemplate");

        Producer producer = (Producer) applicationContext.getBean("producer");

        try {
            producer.SendTransaction();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
