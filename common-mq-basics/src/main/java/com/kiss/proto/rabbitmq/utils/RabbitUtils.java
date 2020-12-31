package com.kiss.proto.rabbitmq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author zhangziyao
 * @date 2020/12/23 15:12
 */
public class RabbitUtils {

    private static final ConnectionFactory CONNECTION_FACTORY = new ConnectionFactory();

    static {
        CONNECTION_FACTORY.setHost("49.232.166.207");
        CONNECTION_FACTORY.setPort(5672);
        CONNECTION_FACTORY.setUsername("guest");
        CONNECTION_FACTORY.setPassword("guest");
        CONNECTION_FACTORY.setVirtualHost("/");
    }

    public static Connection getConnection() {
        try {
            return CONNECTION_FACTORY.newConnection();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
