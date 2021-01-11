package com.kiss.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangziyao
 * @date 2021/1/11
 */
@Data
public class KafkaConfig {

    private String bootstrapServers;
    private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
    private String bufferMemory = "33554432";//默认34m
    private String batchSize = "16384";//默认16K
    private long sessionTimeoutMs = 10000;
    private int maxPollRecords = 50;
    private int lingerMs = 10;
}