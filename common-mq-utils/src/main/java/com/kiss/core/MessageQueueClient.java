package com.kiss.core;

/**
 * @author zhangziyao
 */
public interface MessageQueueClient {

    /**
     * 消息发送
     *
     * @param topic    发送主题
     * @param messages 消息内容
     */
    public void send(String topic, Object messages);
}
