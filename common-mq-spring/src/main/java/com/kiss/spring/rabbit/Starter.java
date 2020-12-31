package com.kiss.spring.rabbit;

import com.kiss.spring.rabbit.conf.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhangziyao
 * @date 2020/12/26 15:05
 */
@Slf4j
public class Starter {

    public static void main(String[] args) {
        //配置文件整合rabbitmq
        // ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:rabbitmq.xml");
        //注解整合rabbitmq
        ApplicationContext context = new AnnotationConfigApplicationContext(RabbitConfig.class);
    }


}
