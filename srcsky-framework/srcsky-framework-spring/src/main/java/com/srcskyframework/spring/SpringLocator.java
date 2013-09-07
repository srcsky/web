package com.srcskyframework.spring;

import com.srcskyframework.core.ActionContext;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * Spring 容器 定位器
 */
public class SpringLocator {
    private static ApplicationContext applicationContext;
    private static boolean initialized = false;
    private final static byte[] lock = new byte[0];

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            synchronized (lock) {
                if (applicationContext == null && ActionContext.getContext() != null) {
                    applicationContext = WebApplicationContextUtils.getWebApplicationContext(ActionContext.getContext().getServletContext(), "org.springframework.web.servlet.FrameworkServlet.CONTEXT.Spring Dispatcher");
                    initialized = true;
                }
                if (applicationContext == null) {
                    applicationContext = new ClassPathXmlApplicationContext("classpath:config/spring/application*.xml");
                    initialized = true;
                }
            }
        }
        return applicationContext;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        if (null != (SpringLocator.applicationContext = applicationContext)) {
            initialized = true;
        }
    }

    public static <T> T getBean(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return BeanFactoryUtils.beanOfType(getApplicationContext(), clazz);
    }

    public static void main(String[] args) {
    }
}