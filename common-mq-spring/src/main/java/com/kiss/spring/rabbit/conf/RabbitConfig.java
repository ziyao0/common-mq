package com.kiss.spring.rabbit.conf;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.spring.rabbit.listener.MessageListener;
import com.kiss.spring.rabbit.utils.LoadPro;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangziyao
 * @date 2020/12/26 15:57
 */
@Configuration
@ComponentScan(basePackages = "com.kiss.spring.rabbit")
@Slf4j
public class RabbitConfig {

    /**
     * 创建rabbitmq连接工厂
     *
     * @return {@link ConnectionFactory}
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        Map<?, ?> pro;
        try {
            pro = LoadPro.getPro("rabbitmq.properties");
        } catch (IOException e) {
            pro = new HashMap<>();
            log.error("error:", e);
        }
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setHost((String) pro.get("rabbitmq.host"));
        connectionFactory.setPort(Integer.parseInt((String) pro.get("rabbitmq.port")));
        connectionFactory.setUsername((String) pro.get("rabbitmq.username"));
        connectionFactory.setPassword((String) pro.get("rabbitmq.password"));
        connectionFactory.setVirtualHost((String) pro.get("rabbitmq.vhost"));
        //开启confirm模式
        connectionFactory.setPublisherConfirms(true);
        //开启return模式
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    /**
     * rabbitmq 实现amqp 便携式管理操作
     *
     * @param connectionFactory {@link ConnectionFactory} 连接工厂
     * @return 返回 {@link RabbitAdmin}
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * rabbitTemplate
     *
     * @param connectionFactory {@link ConnectionFactory} 连接工厂
     * @return {@link RabbitTemplate}
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    /**
     * 设置监听器
     *
     * @param connectionFactory connectionFactory {@link ConnectionFactory} 连接工厂
     * @return 返回 {@link SimpleMessageListenerContainer}
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        //添加监听队列
        simpleMessageListenerContainer.addQueueNames("dlx.queue.admin", "dlx.queue.last.admin");
        //添加并发使用数量 默认为1
        simpleMessageListenerContainer.setConcurrentConsumers(5);
        //最大并发消费者数量
        simpleMessageListenerContainer.setMaxConcurrentConsumers(15);
        //设置是否重回队列
        simpleMessageListenerContainer.setDefaultRequeueRejected(false);
        //设置签收模式
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置非独占模式
        simpleMessageListenerContainer.setExclusive(false);
        //设置consumer 未被ack数量
        simpleMessageListenerContainer.setPrefetchCount(1);
        //接收到消息后出发 后置处理器
        simpleMessageListenerContainer.setAfterReceivePostProcessors(message -> {
            message.getMessageProperties().getHeaders().put("接收到消息ID为：", message.getMessageProperties().getDeliveryTag());
            return message;
        });
        //设置consumer 的 tag
        simpleMessageListenerContainer.setConsumerTagStrategy(new ConsumerTagStrategy() {

            private final AtomicInteger consumer = new AtomicInteger(1);

            @Override
            public String createConsumerTag(String queue) {
                return String.format("consumer:%s:%d", queue, consumer.getAndIncrement());
            }
        });
        //设置监听器
        simpleMessageListenerContainer.setMessageListener(new MessageListener());
        return simpleMessageListenerContainer;
    }

    /**
     * 声明队列：参数一：对列名称
     * 参数二：是否持久化
     * 参数三：是否独占
     * 参数四：是否自动删除
     * 参数五：参数 {@link Map}
     *
     * @return 返回queue
     */
    @Bean
    public Queue queue() {
        return new Queue(QueueConstants.QUEUE_QQ, true, false, false, null);
    }

    /**
     * 声明交换机: 参数一：交换机名称
     * 参数二：是否持久化
     * 参数三：是否自动删除
     *
     * @return {@link Exchange}
     */
    @Bean
    public Exchange exchange() {
        return new TopicExchange(QueueConstants.AMQ_TOPIC, true, false);
    }

    /**
     * 绑定: 参数一：对列名称
     * 参数二：绑定类型
     * 参数三：交换机名称
     * 参数四：路由key
     * 参数五：其他参数 {@link Map}
     *
     * @return {@link Binding}
     */
    @Bean
    public Binding binding() {
        return new Binding(QueueConstants.QUEUE_QQ, Binding.DestinationType.QUEUE, QueueConstants.AMQ_TOPIC, "*.qq.#", null);
    }
}
