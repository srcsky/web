package com.srcskyframework.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-4-7
 * Time: 下午5:18
 * 解决Hibernate延迟加载
 */

public class OpenSessionInViewFilter extends org.springframework.orm.hibernate4.support.OpenSessionInViewFilter {

    protected SessionFactory lookupSessionFactory() {
        if (logger.isDebugEnabled()) {
            //logger.debug("Using SessionFactory '" + getSessionFactoryBeanName() + "' for OpenSessionInViewFilter");
        }
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        return wac.getBean(getSessionFactoryBeanName(), SessionFactory.class);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        SessionFactory sessionFactory = lookupSessionFactory(request);
        boolean participate = false;

        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate flag.
            participate = true;
        } else {
            // logger.debug("Opening Hibernate Session in OpenSessionInViewFilter");
            Session session = openSession(sessionFactory);
            TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (!participate) {
                SessionHolder sessionHolder =
                        (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
                // logger.debug("Closing Hibernate Session in OpenSessionInViewFilter");
                SessionFactoryUtils.closeSession(sessionHolder.getSession());
            }
        }
    }
}
