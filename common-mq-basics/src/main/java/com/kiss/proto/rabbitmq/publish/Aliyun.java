package com.kiss.proto.rabbitmq.publish;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author zhangziyao
 * @date 2020/12/24 13:59
 */
@Slf4j
public class Aliyun {
    public static void main(String[] args) throws IOException {
        //获取连接
        Connection conn = RabbitUtils.getConnection();
        //获取channel
        Channel channel = conn.createChannel();
        //声明队列
        channel.queueDeclare(QueueConstants.QUEUE_ALIYUN, false, false, false, null);
        //绑定队列
        channel.queueBind(QueueConstants.QUEUE_ALIYUN, QueueConstants.AMQ_FANOUT, "");

        channel.basicConsume(QueueConstants.QUEUE_ALIYUN, false, new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body);
                log.info("阿里云邮箱接受到的消息为：{}", msg);
                log.info("消息id为：{}", envelope.getDeliveryTag());
                //手动签收消息
                channel.basicAck(envelope.getDeliveryTag(), false);

            }
        });
    }
}
