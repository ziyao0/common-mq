package com.kiss.proto.rabbitmq.publish;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 发布订阅模式
 *
 * @author zhangziyao
 * @date 2020/12/23 14:34
 */
@Slf4j
public class Email {


    public static void main(String[] args) throws IOException, TimeoutException {
        //获取连接
        Connection conn = RabbitUtils.getConnection();

        Channel channel = conn.createChannel();
        String message = "发布订阅模式!!!!";
        channel.basicPublish(QueueConstants.AMQ_FANOUT, "", null, message.getBytes());
        log.info("消息发送成功!!!");
        channel.close();
        conn.close();
    }
}
