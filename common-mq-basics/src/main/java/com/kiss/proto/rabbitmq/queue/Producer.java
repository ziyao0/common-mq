package com.kiss.proto.rabbitmq.queue;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangziyao
 * @date 2020/12/23 14:34
 */
@Slf4j
public class Producer {


    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection conn = RabbitUtils.getConnection();

        Channel channel = conn.createChannel();

        String message = "队列模式!!!";
        //处理完一个取一个
        channel.basicQos(1);
        channel.basicPublish("", QueueConstants.AMQ_QUEUE, null, message.getBytes(StandardCharsets.UTF_8));
        log.info("消息发送成功!!!");
        channel.close();
        conn.close();
    }
}
