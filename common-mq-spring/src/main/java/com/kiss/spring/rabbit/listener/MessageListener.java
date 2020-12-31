package com.kiss.spring.rabbit.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听器
 *
 * @author zhangziyao
 * @date 2020/12/25 15:46
 */
@Component
@Slf4j
public class MessageListener implements ChannelAwareMessageListener {
    /**
     * @param message {@link Message}消息参数
     * @param channel {@link Channel#basicNack(long, boolean, boolean)} 参数一：消息id
     *                参数二：是的全部确认
     *                参数三：是的重回队列
     * @throws Exception 异常信息
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            log.info("接收到的消息为：{}", message);
            //消息确认机制
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            log.error("error:", e);
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, false);
        }
    }
}
