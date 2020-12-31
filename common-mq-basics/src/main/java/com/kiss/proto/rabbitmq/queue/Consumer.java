package com.kiss.proto.rabbitmq.queue;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zhangziyao
 * @date 2020/12/23 14:47
 */
@Slf4j
public class Consumer {
    public static void main(String[] args) throws IOException {
        //获取连接
        Connection conn = RabbitUtils.getConnection();

        Channel channel = conn.createChannel();

        channel.queueDeclare(QueueConstants.AMQ_QUEUE, false, false, false, null);

        channel.basicConsume(QueueConstants.AMQ_QUEUE, false, new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                log.info("接受到的消息为：{}", msg);
                log.info("消息id为：{}", envelope.getDeliveryTag());
                //手动签收消息
                channel.basicAck(envelope.getDeliveryTag(), false);

            }
        });
    }
}
