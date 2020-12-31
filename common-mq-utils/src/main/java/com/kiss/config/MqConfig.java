package com.kiss.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zhangziyao
 */
@Configuration
@ConditionalOnProperty
@ConfigurationProperties(prefix = "com.kiss.mq")
@Data
@NoArgsConstructor
@ToString
public class MqConfig {

    private String prefix;

    private String mqType;

    @Resource
    private RabbitConfig rabbitConfig;

    @Resource
    private RocketConfig rocketConfig;
}
