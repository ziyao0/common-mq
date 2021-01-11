package com.kiss.proto.kafka.quickstart;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @author zhangziyao
 * @date 2021/1/5 10:06
 */
@Slf4j
public class Consumer {

    private static final String KAFKA_TOPIC = "kafka-topic";

    private static final String GROUP_ID = "defaultGroup0";

    public static void main(String[] args) {
        Properties properties = new Properties();

        //配置broker地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.201.73:9092");
        //设置消费者组ID
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        //设置自动提交offset的时间
        //properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,100);
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
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        //一次poll最大拉取消息的条数，如果消费者处理速度很快，可以设置大点，如果处理速度一般，可以设置小点
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);

        //设置序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        //订阅主题
        kafkaConsumer.subscribe(Collections.singletonList(KAFKA_TOPIC));
        //指定分区消费
//        kafkaConsumer.assign(Collections.singletonList(new TopicPartition(KAFKA_TOPIC, 0)));
        //消息回溯
//        kafkaConsumer.assign(Collections.singletonList(new TopicPartition(KAFKA_TOPIC, 0)));
//        kafkaConsumer.seekToBeginning(Collections.singletonList(new TopicPartition(KAFKA_TOPIC, 0)));
        //指定offset消费
//        kafkaConsumer.assign(Collections.singletonList(new TopicPartition(KAFKA_TOPIC, 0)));
//        kafkaConsumer.seek(new TopicPartition(KAFKA_TOPIC, 0), 3);

        //从指定时间点开始消费

//        List<PartitionInfo> topicPartitions = kafkaConsumer.partitionsFor(KAFKA_TOPIC);
//        //从1小时前开始消费
//        long fetchDataTime = new Date().getTime() - 1000 * 60 * 10;
//        Map<TopicPartition, Long> map = new HashMap<>();
//        for (PartitionInfo par : topicPartitions) {
//            map.put(new TopicPartition(KAFKA_TOPIC, par.partition()), fetchDataTime);
//        }
//        Map<TopicPartition, OffsetAndTimestamp> parMap = kafkaConsumer.offsetsForTimes(map);
//        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : parMap.entrySet()) {
//            TopicPartition key = entry.getKey();
//            OffsetAndTimestamp value = entry.getValue();
//            if (key == null || value == null) continue;
//            Long offset = value.offset();
//            System.out.println("partition-" + key.partition() + "|offset-" + offset);
//            System.out.println();
//            //根据消费里的timestamp确定offset
//            if (value != null) {
//                kafkaConsumer.assign(Collections.singletonList(key));
//                kafkaConsumer.seek(key, offset);
//            }
//        }
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                log.info("*************************************************************************");
                log.info("partition:{}", record.partition());
                log.info("topic:{}", record.topic());
                log.info("offset:{}", record.offset());
                log.info("key:{}", record.key());
                log.info("{}", record.value());
            }
            if (records.count() > 0) {

                //同步提交
//                kafkaConsumer.commitSync();
                //异步提交
                kafkaConsumer.commitAsync((offsets, exception) -> {
                    if (!StringUtils.isEmpty(exception)) {
                        log.info("offsets:{}", offsets);
                        log.info("exception:{}", exception.getMessage());
                    }
                });
            }
        }
    }
}
