package com.srcskyframework.helper;


import com.srcskyframework.core.Enterprise;
import com.srcskyframework.exception.LogicException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-7-8
 * Time: 下午4:02
 * 断言类
 */
public class AssertHelper {

    public static boolean isTrue(boolean expression, Integer code, String key, String message) {
        if (expression) {
            throw new LogicException(code, key, message);
        }
        return expression;
    }

    public static boolean isTrue(boolean expression, String message) {
        return isTrue(expression, -1, "message", message);
    }

    public static boolean isTrue(boolean expression, Integer code, String message) {
        return isTrue(expression, code, "message", message);
    }

    public static boolean isTrue(boolean expression, String key, String message) {
        return isTrue(expression, -1, key, message);
    }

    public static boolean isEmpty(Object object, String message) {
        return isTrue(ValidHelper.isEmpty(object), message);
    }

    public static boolean isEmpty(Enterprise input, String field, String message) {
        return isTrue(input.isEmpty(field), field, message);
    }

    public static boolean isEmpty(Enterprise input, String field, String key, String message) {
        return isTrue(input.isEmpty(field), key, message);
    }

    public static boolean isEquals(Enterprise input, String field, Object value, String message) {
        return isTrue(input.equals(field, value), "message", message);
    }

    public static boolean isNotEquals(Enterprise input, String field, Object value, String message) {
        return isTrue(!input.equals(field, value), "message", message);
    }

    public static boolean isEquals(Enterprise input, String field, Object value, String key, String message) {
        return isTrue(input.equals(field, value), key, message);
    }

    public static boolean isNotEquals(Enterprise input, String field, Object value, String key, String message) {
        return isTrue(!input.equals(field, value), key, message);
    }

    /**
     * Ajax　断言
     *
     * @param expression
     * @param message
     * @return
     */
    public static boolean isTrueAjax(boolean expression, String message) {
        Enterprise input = WebHelper.getInput();
        if (expression) {
            input.setSuccess(false);
            input.setMessage(message);
        }
        return expression;
    }
}
