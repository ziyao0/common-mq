package com.kiss.springboot.rabbitmq.component;

import com.kiss.mq.model.contstants.QueueConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangziyao
 * @date 2020/12/24 16:24
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test() {
        rabbitTemplate.convertAndSend(QueueConstants.AMQ_TOPIC, "beijing.126.zhang.ziyao", "hello springboot!!!");
    }
}
