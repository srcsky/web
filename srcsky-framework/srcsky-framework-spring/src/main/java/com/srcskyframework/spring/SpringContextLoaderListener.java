package com.srcskyframework.spring;

import com.srcskyframework.core.Configuration;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午8:41
 * Email: z82422@gmail.com
 * 通过 Web 监听器 为 SpringLocator 初始化
 */

//@WebListener
public class SpringContextLoaderListener extends ContextLoaderListener {
    private final static Logger logger = Logger.getLogger(SpringContextLoaderListener.class);

    public void contextInitialized(ServletContextEvent event) {
        // 初始化 LOG4J
        Configuration.initLog4j();
        //初始化
        /*RefreshUtility.servletContext = event.getServletContext();
        if (Configuration.get().getBoolean("h2.enable") && !H2ServerManager.isRun) {
            H2ServerManager.startServer();
        }*/
        // Spring配置文件
        event.getServletContext().setInitParameter("contextConfigLocation", "classpath*:config/spring/application-main.xml");
        super.contextInitialized(event);
        SpringLocator.setApplicationContext(WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext()));
        logger.debug("Succeed to set application context to spring locator.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        //H2ServerManager.stopServer();
    }
}