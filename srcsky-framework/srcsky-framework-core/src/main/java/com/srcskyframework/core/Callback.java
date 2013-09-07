package com.srcskyframework.core;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-3-14
 * Time: 下午1:46
 * To change this template use File | Settings | File Templates.
 */
public interface Callback<T> {
    public T execute(Object object);
}
