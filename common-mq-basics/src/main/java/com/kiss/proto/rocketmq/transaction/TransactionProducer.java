package com.kiss.proto.rocketmq.transaction;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.*;

/**
 * @author zhangziyao
 * @date 2020/12/30 13:51
 */
@Slf4j
public class TransactionProducer {


    public static void main(String[] args) throws MQClientException {
        TransactionListenerImpl transactionListener = new TransactionListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer("transaction");
        producer.setNamesrvAddr("49.232.166.207:9876");

        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setExecutorService(executorService);
        producer.setTransactionListener(transactionListener);
        producer.start();
        String[] tags = {"A", "B", "C"};
        for (int i = 0; i < 6; i++) {
            Message message = new Message("transactionTopic", tags[i % tags.length], "事务消息！！！".getBytes());
            SendResult result = producer.sendMessageInTransaction(message, args);
            log.info("发送状态：{}", result.getSendStatus());
        }
        producer.shutdown();
    }
}
