package com.kiss.api;

/**
 * @author zhangziyao
 * @date 2020/12/9
 */
public class MessageException extends Exception {

    private static final long serialVersionUID = 6163437284195358489L;

    public MessageException() {
        super();
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageException(Throwable cause) {
        super(cause);
    }
}
