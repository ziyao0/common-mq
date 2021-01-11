package com.kiss.client;

import com.kiss.config.RabbitConfig;
import com.kiss.mq.model.enums.WorkMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

/**
 * rabbitmq 客户端
 *
 * @author zhangziyao
 */
@Configuration
@ConditionalOnProperty(value = "com.kiss.mq.mqType", havingValue = "rabbitmq")
@Slf4j
public class RabbitClient {

    @Autowired(required = false)
    @Lazy
    private RabbitConfig rabbitConfig;

    /**
     * 创建连接工厂
     *
     * @return {@link ConnectionFactory}
     */
    @Bean
    @Lazy
    public ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(rabbitConfig.getHost());
        factory.setPort(rabbitConfig.getPort());
        factory.setUsername(rabbitConfig.getUsername());
        factory.setPassword(rabbitConfig.getPassword());
        factory.setVirtualHost(rabbitConfig.getVirtualHost());
        factory.setPublisherReturns(true);
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return factory;
    }

    @Bean
    @Lazy
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    /**
     * 创建队列
     *
     * @param queueName 队列名称
     * @return 返回队列名称
     */
    public String createQueue(RabbitAdmin rabbitAdmin, String queueName, Map<String, Object> ages) {
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
    public String createExchange(RabbitAdmin rabbitAdmin, String ExchangeName, WorkMode workMode) {
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

    /**
     * 绑定队列和交换机
     *
     * @param rabbitAdmin rabbitmq管理工具 {@link org.springframework.amqp.core.AmqpAdmin}
     * @param queueName   队列名称
     * @param topicName   主题名称
     * @param agrs        参数
     * @param workMode    工作模式 {@link com.kiss.annotation.WorkMode}
     */
    @Bean
    @Lazy
    public void binding(RabbitAdmin rabbitAdmin, String queueName, String topicName, Map<String, Object> agrs, com.kiss.annotation.WorkMode workMode) {
        if (com.kiss.annotation.WorkMode.DIRECT.equals(workMode)) {
            rabbitAdmin.declareBinding(new org.springframework.amqp.core.Binding(createQueue(rabbitAdmin, queueName, agrs)
                    , org.springframework.amqp.core.Binding.DestinationType.QUEUE
                    , createExchange(rabbitAdmin, topicName, WorkMode.direct)
                    , topicName
                    , null));
        } else {
            rabbitAdmin.declareBinding(new org.springframework.amqp.core.Binding(createQueue(rabbitAdmin, queueName, agrs)
                    , org.springframework.amqp.core.Binding.DestinationType.QUEUE
                    , createExchange(rabbitAdmin, topicName, WorkMode.fanout)
                    , ""
                    , null));
        }
    }

    /**
     * 发送消息
     *
     * @param rabbitTemplate {@link RabbitTemplate} rabbitmq模板
     * @param topicName      topic名称
     * @param workMode       工作模式 {@link com.kiss.annotation.WorkMode}
     * @param message        消息内容
     */
    public void sendRabbitMessage(RabbitTemplate rabbitTemplate, String topicName, com.kiss.annotation.WorkMode workMode, Object message) {
        if (com.kiss.annotation.WorkMode.DIRECT.equals(workMode)) {
            rabbitTemplate.convertAndSend(topicName, topicName, message);
        } else {
            rabbitTemplate.convertAndSend(topicName, "", message);
        }
    }
}
