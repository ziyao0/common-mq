package com.kiss.springboot.rabbitmq.config;

import com.kiss.mq.model.contstants.QueueConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangziyao
 * @date 2020/12/25 16:04
 */
@Configuration
public class RabbitConfig {


    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private String port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;
    @Value("${spring.rabbitmq.publisher-returns}")
    private String publisherReturn;
    @Value("${spring.rabbitmq.publisher-confirm-type}")
    private CachingConnectionFactory.ConfirmType PublisherConfirmType;

    /**
     * 声明连接工厂
     *
     * @return 返回connectionFactory
     */
    @Bean
    public ConnectionFactory getConnectionFactory() {

        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(host);
        factory.setPort(Integer.parseInt(port));
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setPublisherReturns(Boolean.parseBoolean(publisherReturn));
        factory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return factory;
    }

    /**
     * 声明rabbitmq 管理员
     *
     * @param connectionFactory 连接工厂
     * @return 返回rabbitAdmin
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 声明exchange
     *
     * @return 返回exchange
     */
    @Bean
    public Exchange exchange() {

        return ExchangeBuilder.topicExchange(QueueConstants.AMQ_TOPIC).durable(true).build();
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QueueConstants.QUEUE_126).build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with("*.126.#").noargs();
    }
}
