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
@ConditionalOnProperty(prefix = "com.kiss.mq.mqType", havingValue = "rabbit")
@ConfigurationProperties(prefix = "com.kiss.mq.rabbit")
@Data
@NoArgsConstructor
@ToString
public class RabbitConfig {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String virtualHost;
}
