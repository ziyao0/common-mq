package com.kiss.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息监听器
 *
 * @author zhangziyao
 * @date 2021/1/6
 */
@Slf4j
public abstract class MessageChannelListener implements ChannelAwareMessageListener,
        MessageListenerConcurrently, AcknowledgingMessageListener<String, Object> {

    @Override
    public void onMessage(Message message, Channel channel) {
        try {
            channel.basicQos(1);
            this.onMessage(new com.kiss.core.Message(new String(message.getBody())));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            log.info("rabbitmq consumer error：{}", e.getMessage(), e);
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, false);
            } catch (IOException ioException) {
                log.info("rabbitmq consumer nack exception...", e);
            }
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        List<com.kiss.core.Message> messages = new ArrayList<>();
        for (MessageExt msg : msgs) {
            messages.add(new com.kiss.core.Message(new String(msg.getBody())));
        }
        try {
            this.onMessage(messages);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.info("rocketmq consumer error：{}", e.getMessage(), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    @Override
    public void onMessage(ConsumerRecord<String, Object> data, Acknowledgment ack) {
        com.kiss.core.Message message = new com.kiss.core.Message(data.value().toString());
        try {
            this.onMessage(message);
            ack.acknowledge();
        } catch (Exception e) {
            log.info("kafka consumer error：{}", e.getMessage(), e);
            ack.nack(2000);
        }
    }

    public abstract void onMessage(com.kiss.core.Message message) throws Exception;

    public abstract void onMessage(List<com.kiss.core.Message> messages) throws Exception;
}
