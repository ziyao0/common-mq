package com.kiss.spring.rabbit.conf;

import com.kiss.mq.model.enums.ArgKeys;
import com.kiss.mq.model.enums.WorkMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangziyao
 * @date 2020/12/26 9:10 下午
 */
@Component
@Slf4j
public class RabbitAdminConfig implements InitializingBean {


    @Autowired
    private RabbitAdmin rabbitAdmin;


    /**
     * 创建队列
     *
     * @param queueName 队列名称
     * @return 返回队列名称
     */
    public String createQueue(String queueName, Map<String, Object> ages) {
        log.info("创建队列：{}", queueName);
        rabbitAdmin.declareQueue(new Queue(queueName, true, false, false, ages));
        return queueName;
    }

    /**
     * 创建交换机
     *
     * @param ExchangeName 交换机名称
     * @param workMode     交换机工作模式 参考 {@link WorkMode}
     * @return 返回交换机名称
     */
    public String createExchange(String ExchangeName, WorkMode workMode) {
        log.info("创建交换机：{}", ExchangeName);
        switch (workMode) {
            case fanout:
                rabbitAdmin.declareExchange(new FanoutExchange(ExchangeName, true, false, null));
                break;
            case direct:
                rabbitAdmin.declareExchange(new DirectExchange(ExchangeName, true, false, null));
                break;
            case topic:
                rabbitAdmin.declareExchange(new TopicExchange(ExchangeName, true, false, null));
                break;
            default:
                log.info("没有要创建的交换机类型：{}", workMode);
        }
        return ExchangeName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Map<String, Object> agrs = new HashMap<>();
        agrs.put(ArgKeys.X_MESSAGE_TTL.getKey(), 10000);
        agrs.put(ArgKeys.X_DEAD_LETTER_EXCHANGE.getKey(), "dlx.exchange.last.admin");
        agrs.put(ArgKeys.X_DEAD_LETTER_ROUTING_KEY.getKey(), "dlx.zhang.admin");
        agrs.put(ArgKeys.X_MAX_LENGTH.getKey(), 5);


        rabbitAdmin.declareBinding(new Binding(createQueue("dlx.queue.admin", agrs)
                , Binding.DestinationType.QUEUE
                , createExchange("dlx.exchange.admin", WorkMode.topic)
                , "dlx.#"
                , null));
        rabbitAdmin.declareBinding(new Binding(createQueue("dlx.queue.last.admin", null)
                , Binding.DestinationType.QUEUE
                , createExchange("dlx.exchange.last.admin", WorkMode.topic)
                , "dlx.#"
                , null));

    }
}
