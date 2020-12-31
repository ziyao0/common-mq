package com.kiss.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangziyao
 */
@Configuration
@ConditionalOnProperty(prefix = "com.kiss.mq.mqType", havingValue = "rocket")
@ConfigurationProperties(prefix = "com.kiss.mq.rocket")
@Data
@NoArgsConstructor
@ToString
public class RocketConfig {

    private String nameSrvAddr;
}
