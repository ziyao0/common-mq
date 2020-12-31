package com.kiss.proto.rocketmq.transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 事务监听器
 *
 * @author zhangziyao
 * @date 2020/12/30 13:54
 */
public class TransactionListenerImpl implements TransactionListener {
    /**
     * 执行本地事务方法
     *
     * @param msg 消息内容
     * @param arg 参数
     * @return 返回本地事务状态
     */
    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

        String tags = msg.getTags();
        if (StringUtils.contains(tags, "A")) {
            return LocalTransactionState.COMMIT_MESSAGE;
        } else if (StringUtils.contains(tags, "B")) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } else {
            return LocalTransactionState.UNKNOW;
        }
    }

    /**
     * 检查事务 默认执行5次
     *
     * @param msg 消息内容
     * @return 返回事务状态
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String tags = msg.getTags();
        if (StringUtils.contains(tags, "C")) {
            return LocalTransactionState.COMMIT_MESSAGE;
        } else {
            return LocalTransactionState.UNKNOW;
        }
    }
}
