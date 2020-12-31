package com.kiss.proto.rabbitmq.topic;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zhangziyao
 * @date 2020/12/24 14:13
 */
@Slf4j
public class _aliyun {

    public static void main(String[] args) throws IOException {

        Connection connection = RabbitUtils.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QueueConstants.QUEUE_ALIYUN, false, false, false, null);
        channel.queueBind(QueueConstants.QUEUE_ALIYUN, QueueConstants.AMQ_TOPIC, "*.126.#");

        channel.basicQos(1);

        channel.basicConsume(QueueConstants.QUEUE_ALIYUN, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                log.info("阿里云邮箱接受到的内容为：{}", new String(body));
                log.info("消息ID为：{}", envelope.getDeliveryTag());
                log.info("consumerTag：{}", consumerTag);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }
}
