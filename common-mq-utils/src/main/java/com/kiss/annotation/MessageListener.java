package com.kiss.annotation;

import org.springframework.messaging.handler.annotation.MessageMapping;

import java.lang.annotation.*;

/**
 * @author zhangziyao
 * @date 2020/12/9
 */

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface MessageListener {
    /**
     * topic
     */
    String topic() default "";

    /**
     * queue
     */
    String queue() default "";

    /**
     * {@link QueueType#DIRECT} 直连模式
     * {@link QueueType#FANOUT} 广播模式
     *
     * @return 返回创建队列模式
     */
    QueueType type() default QueueType.DIRECT;
}
