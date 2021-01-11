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
public @interface MessageChannelListener {
    /**
     * topic
     */
    String topic() default "";

    /**
     * queue
     */
    String queue() default "";

    /**
     * {@link WorkMode#DIRECT} 路由模式
     * <br>
     * {@link WorkMode#FANOUT} 广播模式
     *
     * @return 返回创建队列模式
     */
    WorkMode type() default WorkMode.DIRECT;
}
