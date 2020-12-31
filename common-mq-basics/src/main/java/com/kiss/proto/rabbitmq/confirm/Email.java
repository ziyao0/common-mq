package com.kiss.proto.rabbitmq.confirm;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangziyao
 * @date 2020/12/24 14:06
 */
@Slf4j
public class Email {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.confirmSelect();

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) {
                log.info("消息签收，消息id为：{}", deliveryTag);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) {
                log.info("消息拒签，消息id为：{}", deliveryTag);
            }
        });
        channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {

            log.error("消息内容：{}", new String(body));
            log.error("返回状态码：{}", replyCode);
            log.error("交换机名称：{}", exchange);
            log.error("路由key：{}", routingKey);
            log.error("回复文本：{}", replyText);
        });
        Map<String, String> emailData = new HashMap<>();
        emailData.put("email.qq.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.126.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.163.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.aliyun.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.qq.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.126.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.163.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.aliyun.cate.20201225", "20201225的餐厅的美食很不错！！！");

        for (
                String key : emailData.keySet()) {
            channel.basicPublish(QueueConstants.AMQ_TOPIC, key, true, null, emailData.get(key).getBytes());
        }

    }
}
