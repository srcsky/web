package com.srcskyframework.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:47
 * Email: z82422@gmail.com
 * 异常 基类
 */
public class Exception extends RuntimeException implements Serializable {
    private Boolean isFirst;
    private Throwable rootCause;
    /*异常代码*/
    private Integer code = 0;

    public Exception() {
        isFirst = true;
    }

    public Exception(String message) {
        this(0, message, null);
    }

    public Exception(Integer code, String message) {
        this(code, message, null);
    }

    public Exception(String message, Throwable rootCause) {
        this(0, message, rootCause);
    }

    public Exception(Integer code, Throwable rootCause) {
        this(code, rootCause.getMessage(), rootCause);
    }

    public Exception(Integer code, String message, Throwable rootCause) {
        super(message);
        this.code = code;
        this.rootCause = rootCause;
        if (null != this.rootCause)
            isFirst = false;
    }

    public Exception(Throwable rootCause) {
        super(rootCause);
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public String getStackTraceString() {
        StringWriter s = new StringWriter();
        printStackTrace(new PrintWriter(s));
        return s.toString();
    }

    public void printStackTrace() {
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            super.printStackTrace(s);
            if (rootCause != null)
                rootCause.printStackTrace(s);
            if (isFirst || !(rootCause instanceof Exception))
                s.println("-----------------------------");
        }
    }

    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            super.printStackTrace(s);
            if (rootCause != null)
                rootCause.printStackTrace(s);
            if (isFirst || !(rootCause instanceof Exception))
                s.println("-----------------------------");
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}