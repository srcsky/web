package com.srcskyframework.exception;

import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.ValidHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * Web 逻辑 异常
 */
public class LogicException extends Exception {
    private Enterprise input = new Enterprise();
    private String key;
    private String message;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMessage() {
        return null != message ? message : super.getMessage();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Enterprise getInput() {
        return input;
    }

    public LogicException() {
    }

    public LogicException(String message) {
        super(message);
    }

    public LogicException(Integer code, String message) {
        super(code, message);
    }

    public LogicException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    public LogicException(Integer code, Throwable rootCause) {
        super(code, rootCause);
    }

    public LogicException(Integer code, String key, String message) {
        super(code, message);
        this.key = key;
        this.message = message;
    }

    public LogicException(Throwable rootCause) {
        super(rootCause);
    }

    /**
     * 用户不能为空
     *
     * @return
     */
    public static LogicException generateExceptionByCannotForEmpty(String... use) {
        return generateException((ValidHelper.isEmpty(use) ? "" : use[0]) + "不能为空");
    }

    /**
     * 没有权限
     *
     * @return
     */
    public static LogicException generateExceptionByPermissionDenied() {
        return generateException("没有权限");
    }

    /**
     * 生成异常消息
     *
     * @param exception
     * @return
     */
    public static LogicException generateException(String exception) {
        return new LogicException(exception);
    }

    public static LogicException generateException(Integer code, String exception) {
        return new LogicException(code, exception);
    }
}
