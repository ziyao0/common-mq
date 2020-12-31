package com.kiss.proto.rabbitmq.topic;

import com.kiss.mq.model.contstants.QueueConstants;
import com.kiss.proto.rabbitmq.utils.RabbitUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author zhangziyao
 * @date 2020/12/24 14:06
 */
public class Email {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();

        Map<String, String> emailData = new HashMap<>();

        emailData.put("email.qq.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.126.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.163.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.aliyun.cate.20201224", "20201224的餐厅的美食很不错！！！");
        emailData.put("email.qq.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.126.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.163.cate.20201225", "20201225的餐厅的美食很不错！！！");
        emailData.put("email.aliyun.cate.20201225", "20201225的餐厅的美食很不错！！！");

        for (String key : emailData.keySet()) {
            channel.basicPublish(QueueConstants.AMQ_TOPIC, key, null, emailData.get(key).getBytes());
        }

        channel.close();
        connection.close();
    }
}
