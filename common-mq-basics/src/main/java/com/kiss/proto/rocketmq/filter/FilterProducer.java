package com.kiss.proto.rocketmq.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * 过滤模式
 * 支持tag过滤
 * 简单sql过滤：支持 OR,IN,AND,BETWEEN,IS NULL,IS NOT IN,>,<,=,<>等
 * 常量支持类型：数值、字符（必须单引号）、null（特殊常量）、布尔值
 * 备注：只有推模式支持sql条件过滤
 *
 * @author zhangziyao
 * @date 2020/12/30 8:57
 */
@Slf4j
public class FilterProducer {

    public static void main(String[] args) {
        DefaultMQProducer producer = null;
        try {
            producer = new DefaultMQProducer("quickstart_group1");
            producer.setNamesrvAddr("49.232.166.207:9876");
            producer.start();
            String[] tags = {"A", "B", "C"};
            for (int i = 0; i < 10; i++) {
                Message message = new Message("Filter", tags[i % tags.length], ("过滤消息,消息id为：" + i + "！！！").getBytes());
                message.putUserProperty("zhang", i + "");
                SendResult result = producer.send(message);
                log.info("发送状态：{}", result.getSendStatus());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            assert producer != null;
            producer.shutdown();
        }
    }
}
