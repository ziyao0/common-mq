package com.kiss.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * rocketmq 客户端
 *
 * @author zhangziyao
 */
@Configuration
@ConditionalOnProperty(value = "com.kiss.mq.mqType", havingValue = "rocketmq")
@Slf4j
public class RocketClient {


}
