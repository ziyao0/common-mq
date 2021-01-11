package com.kiss.springboot.kafka.init;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.kiss.springboot.kafka.component.Producer;

/**
 * @author zhangziyao
 * @date 2021/1/1 11:03 下午
 */
@Component
public class InitBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        KafkaTemplate<String, String> kafkaTemplate = (KafkaTemplate<String, String>) context.getBean("kafkaTemplate");
        Producer producer = (Producer) context.getBean("producer");
        producer.sendAsSync(kafkaTemplate);
        producer.sendSync(kafkaTemplate);
    }
}
