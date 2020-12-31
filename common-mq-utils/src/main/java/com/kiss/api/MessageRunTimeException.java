package com.kiss.api;

/**
 * @author zhangziyao
 * @date 2020/12/9
 */
public class MessageRunTimeException extends Exception {

    private static final long serialVersionUID = 4376684416639677187L;

    public MessageRunTimeException() {
        super();
    }

    public MessageRunTimeException(String message) {
        super(message);
    }

    public MessageRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageRunTimeException(Throwable cause) {
        super(cause);
    }
}
