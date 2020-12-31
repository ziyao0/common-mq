package com.kiss.mq.model.enums;

import lombok.Getter;

/**
 * @author zhangziyao
 * @date 2020/12/24 9:30
 */
@Getter
public enum WorkMode {

    /**
     * 队列模式
     */
    queue,

    /**
     * 广播模式
     */
    fanout,

    /**
     * 路由模式
     */
    direct,

    /**
     * topic通配符模式
     */
    topic;
}
