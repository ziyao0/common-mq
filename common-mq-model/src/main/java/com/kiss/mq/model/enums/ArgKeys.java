package com.kiss.mq.model.enums;

import lombok.Getter;

/**
 * @author zhangziyao
 * @date 2020/12/27 5:04 下午
 */
@Getter
public enum ArgKeys {

    X_MESSAGE_TTL("x-message-ttl"),
    X_DEAD_LETTER_EXCHANGE("x-dead-letter-exchange"),
    X_DEAD_LETTER_ROUTING_KEY("x-dead-letter-routing-key"),
    X_MAX_LENGTH("x-max-length"),
    ;

    private final String key;

    ArgKeys(String key) {
        this.key = key;
    }
}
