package com.kiss.proto.kafka.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author zhangziyao
 * @date 2021/1/2 11:21 下午
 */
@Slf4j
public class Producer {

    private static final String KAFKA_TOPIC = "kafka-topic";

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Properties properties = new Properties();
        //配置broker地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.201.73:9092");
        /*
        消息ack参数设置
            acks=0；说明producer不需要等待broker确认收到消息后的回复，可以做继续其他操作，性能最高，但是容易丢消息
            acks=1：producer把消息发送到broker，要至少等到leader吧消息写入到log文件后就可以做其他操作
            acks=-1或all：会根据min.insync.replicas（默认为1）的配置，需要把消息写入到配置的副本数都成功写入日志后再做其他操作，消息最不容易丢失，
         */
        properties.put(ProducerConfig.ACKS_CONFIG, "1");

        /*
         *重试机制：发送失败后会重新发送，默认间隔100ms，重试能保证消息的可靠性，但会带来重复发送的问题，所以需要消费端做好消息的幂等性处理
         */
        //重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //重试时间间隔
        properties.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 100);

        //设置本地消息缓存区 默认33554432 32M
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        /*
        kafka本地线程会从缓冲区取数据，批量发送到broker，
        设置批量发送消息的大小，默认值是16384，即16kb，就是说一个batch满了16kb就发送出去
        */
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        /*
        默认值是0，意思就是消息必须立即被发送，但这样会影响性能
        一般设置10毫秒左右，就是说这个消息发送完后会进入本地的一个batch，如果10毫秒内，这个batch满了16kb就会随batch一起被发送出去
        如果10毫秒内，batch没满，那么也必须把消息发送出去，不能让消息的发送延迟时间太长
        */
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        //把key序列化成字节数组
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //把value序列化成字节数组
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        //创建kafka producer
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        for (int i = 0; i < 1000 * 1000; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(KAFKA_TOPIC, 1 + "", "消息id为：" + i);
            //同步发送
            RecordMetadata res = kafkaProducer.send(producerRecord).get();
            log.info("=================================================");
            log.info("消息topic：{}----topic:{}", 100 + i, res.topic());
            log.info("消息offset：{}----offset:{}", 100 + i, res.offset());
            log.info("partition：{}----partition:{}", 100 + i, res.partition());
        }
        kafkaProducer.close();
    }
}
