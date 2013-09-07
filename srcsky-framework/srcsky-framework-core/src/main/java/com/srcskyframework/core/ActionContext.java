package com.srcskyframework.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午9:26
 * Email: z82422@gmail.com
 * 封装 Web 容器 中的 环境变量 与当前线程绑定，以便在任何地方调用
 */
public class ActionContext {
    private static ThreadLocal<ActionContext> contextThreadLocal = new ThreadLocal<ActionContext>();
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext context;

    /**
     * Get current ActionContext.
     *
     * @return ActionContext.
     */
    public static ActionContext getContext() {
        return contextThreadLocal.get();
    }

    /**
     * Initiate all servlet objects as thread local.
     *
     * @param request  HttpServletRequest object.
     * @param response HttpServletResponse object.
     * @param context  ServletContext object.
     */
    public static void setContext(HttpServletRequest request, HttpServletResponse response, ServletContext context) {
        ActionContext contextUtility = new ActionContext();
        contextUtility.setRequest(request);
        contextUtility.setResponse(response);
        contextUtility.setServletContext(context);
        contextThreadLocal.set(contextUtility);
    }

    /**
     * Remove all servlet objects from thread local.
     */
    public static void remove() {
        contextThreadLocal.remove();
    }

    /**
     * Get HttpServletRequest object.
     *
     * @return HttpServletRequest object.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    public <T> T getRequestAttr(String key) {
        return (T) request.getAttribute(key);
    }


    /**
     * Set HttpServletRequest object.
     *
     * @param request HttpServletRequest object.
     */
    void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setRequestAttr(String key, Object value) {
        if (null == value) {
            this.request.removeAttribute(key);
        } else {
            this.request.setAttribute(key, value);
        }
    }

    /**
     * Get HttpServletResponse object.
     *
     * @return HttpServletResponse object.
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * Set HttpServletResponse object.
     *
     * @param response HttpServletResponse object.
     */
    void setResponse(HttpServletResponse response) {
        this.response = response;
    }


    public HttpSession getSession(boolean create) {
        return request.getSession(create);
    }

    public <T> T getSessionAttr(String key) {
        HttpSession session = getSession(false);
        return null == session ? null : (T) session.getAttribute(key);
    }

    public void setSessionAttr(String key, Object value) {
        HttpSession session = getSession(true);
        if (null == value) {
            session.removeAttribute(key);
        } else {
            session.setAttribute(key, value);
        }
    }


    /**
     * Get ServletContext object.
     *
     * @return ServletContext object.
     */
    public ServletContext getServletContext() {
        return context;
    }

    public <T> T getServletContextAttr(String key) {
        return (T) context.getAttribute(key);
    }

    /**
     * Set ServletContext object.
     *
     * @param context ServletContext object.
     */
    void setServletContext(ServletContext context) {
        this.context = context;
    }

    public void setServletContextAttr(String key, Object value) {
        if (null == value) {
            this.context.removeAttribute(key);
        } else {
            this.context.setAttribute(key, value);
        }
    }
}

