package com.srcskyframework.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * Web 逻辑 异常
 */
public class ViewLogicException extends LogicException {

    //非法请求
    public final static ViewLogicException ILLEGAL_REQUEST = new ViewLogicException("非法请求");
    public final static ViewLogicException RECORD_DOES_NOT_EXIST = new ViewLogicException("记录不存在");

    public ViewLogicException() {
    }

    public ViewLogicException(Throwable throwable) {
        super(throwable);
    }

    public ViewLogicException(String message) {
        super(message);
    }

    public ViewLogicException(Integer code, String message) {
        super(code, message);
    }

    public ViewLogicException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public ViewLogicException(Integer code, Throwable rootCause) {
        super(code, rootCause);
    }
}
