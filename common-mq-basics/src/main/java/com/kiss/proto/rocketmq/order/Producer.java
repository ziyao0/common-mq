package com.kiss.proto.rocketmq.order;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * @author zhangziyao
 * @date 2020/12/11
 */
@Slf4j
public class Producer {

    public static void main(String[] args) {
        DefaultMQProducer producer = null;
        try {
            producer = new DefaultMQProducer("quickstart_group1");
            producer.setNamesrvAddr("49.232.166.207:9876");

            producer.start();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
                    Message message = new Message("orderTopic", "orderId_" + i + "----" + j, "KEY" + i,
                            ("订单:" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                    SendResult sendResult = producer.send(message, (list, message1, args1) -> {
                        //相同订单的消息放到同一个队列中 实现消息顺序消费
                        Integer id = (Integer) args1;
                        return list.get(id % list.size());
                    }, i);

                    log.info("发送消息返回结果：{}", sendResult);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert producer != null;
            producer.shutdown();
        }
    }
}
