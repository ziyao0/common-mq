package com.kiss.client;

import com.kiss.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 客户端
 *
 * @author zhangziyao
 * @date 2020/12/9
 */
@Configuration
@ConditionalOnProperty(value = "com.kiss.mq.mqType", havingValue = "kafka")
@Slf4j
public class KafkaClient {

    @Autowired(required = false)
    private KafkaConfig kafkaConfig;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * kafka 生产者参数 {@link ProducerConfig}
     *
     * @return args
     */
    public Map<String, Object> producerProps() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        //ack
        properties.put(ProducerConfig.ACKS_CONFIG, "1");

        //重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //重试时间间隔
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);

        //设置本地消息缓存区 默认33554432 32M
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfig.getBufferMemory());
        /*
        kafka本地线程会从缓冲区取数据，批量发送到broker，
        设置批量发送消息的大小，默认值是16384，即16kb，就是说一个batch满了16kb就发送出去
        */
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaConfig.getBatchSize());

        //默认值是0，意思就是消息必须立即被发送，但这样会影响性能
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        //把key序列化成字节数组
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaConfig.getKeySerializer());
        //把value序列化成字节数组
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaConfig.getValueSerializer());
        return properties;
    }

    /**
     * kafka 消费者参数 {@link ConsumerConfig}
     *
     * @return args
     */
    public Map<String, Object> consumerProps() {
        Map<String, Object> properties = new HashMap<>();

        //配置broker地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        //设置消费者组ID
//        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        //设置是否自动提交 模式为true
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        /*
        当消费主题的是一个新的消费组，或者指定offset的消费方式，offset不存在，那么应该如何消费
        latest(默认) ：只消费自己启动之后发送到主题的消息
        earliest：第一次从头开始消费，以后按照消费offset记录继续消费，这个需要区别于consumer.seekToBeginning(每次都从头开始消费)
        */
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
	    /*
		consumer给broker发送心跳的间隔时间，broker接收到心跳如果此时有rebalance发生会通过心跳响应将rebalance方案下发给consumer
		*/
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        /*
        服务端broker多久感知不到一个consumer心跳就认为他故障了，会将其踢出消费组，
        对应的Partition也会被重新分配给其他consumer，默认是10秒
        */
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfig.getSessionTimeoutMs());
        //一次poll最大拉取消息的条数，如果消费者处理速度很快，可以设置大点，如果处理速度一般，可以设置小点
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfig.getMaxPollRecords());

        //设置序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getKeyDeserializer());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConfig.getValueDeserializer());

        return properties;
    }
}
