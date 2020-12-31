package com.kiss.springboot.rocketmq.conf;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务监听配置类
 *
 * @author zhangziyao
 * @date 2020/12/30 14:50
 */
@Slf4j
@Component
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class TransactionListenerConfig implements RocketMQLocalTransactionListener {

    private ConcurrentHashMap<Object, Message> localTransaction = new ConcurrentHashMap<>();

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object arg) {

//        事务id
        Object tranId = message.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        String destination = arg.toString();
        localTransaction.put(tranId, message);
        log.info("tranId----:{}", tranId);
        log.info("localTransaction----:{}", localTransaction);
//        log.info("message++++++++++++:{}", message);
        log.info("destination++++++++++++:{}", destination);
        org.apache.rocketmq.common.message.Message rocketMessage = RocketMQUtil.convertToRocketMessage(new StringMessageConverter(), "utf-8", destination, message);

        String tags = rocketMessage.getTags();
        if (StringUtils.contains(tags, "A")) {
            return RocketMQLocalTransactionState.COMMIT;
        } else if (StringUtils.contains(tags, "B")) {
            return RocketMQLocalTransactionState.ROLLBACK;
        } else {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        log.info("执行checkLocalTransaction方法.................");
        String transId = message.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID).toString();
        Message originalMessage = localTransaction.get(transId);

        String tags = originalMessage.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TAGS).toString();
        if (StringUtils.contains(tags, "C")) {
            return RocketMQLocalTransactionState.COMMIT;
        } else {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
    }
}
