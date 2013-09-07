/**
 * 宝龙电商
 * com.srcskyframework.spring.servlet
 * DispatcherServlet.java
 *
 * 2013-8-7-下午1:55
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.spring.mvc.servlet;

import com.srcskyframework.core.ActionContext;
import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Constants;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.ValidHelper;
import com.srcskyframework.helper.WebHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * DispatcherServlet
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-7-下午1:55
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class DispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

    private static final String thumbnail_url = "/system/thumbnail";
    private String characterEncoding = "UTF-8";
    private Pattern excludeUrlPattern = Pattern.compile("/static/.*|.*\\.ico");

    public void initFrameworkServlet() throws ServletException {
        super.initFrameworkServlet();
        try {
            characterEncoding = ValidHelper.isEmpty(getServletConfig().getInitParameter("characterEncoding")) ? "UTF-8" : getServletConfig().getInitParameter("characterEncoding");
            if (ValidHelper.isNotEmpty(getServletConfig().getInitParameter("exclude-url-pattern"))) {
                excludeUrlPattern = Pattern.compile(getServletConfig().getInitParameter("exclude-url-pattern"));
            }
            // 设置Web Root 路径
            Constants.WEB_ROOT_ABSOLUTE_PATH = getServletContext().getRealPath("/");
            Constants.WEB_CONTENT_PATH = getServletContext().getContextPath();
            // 修改保存路径
            if (!Configuration.get().isEmpty("web_fileuploader_directory")
                    && Configuration.get().equals("web_fileuploader_absolute", "false")) {
                Configuration.get().set("web_fileuploader_tempdirectory", new File(getServletContext().getRealPath("/static/fileuploader/temp")).getAbsolutePath());
                Configuration.get().set("web_fileuploader_directory", new File(getServletContext().getRealPath("/static/fileuploader")).getAbsolutePath());
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        String url = requestURI.substring(Constants.WEB_CONTENT_PATH.length());
        if (null == excludeUrlPattern || !excludeUrlPattern.matcher(url).find()) {
            // 耗时,记录
            long applicationTime = System.currentTimeMillis();
            // 设置请求编码格式
            request.setCharacterEncoding(characterEncoding);
            // init ActionContext:
            ActionContext.setContext(request, response, getServletContext());
            Enterprise input = WebHelper.getInput(request);
            try {
                super.service(request, response);
            } finally {
                ActionContext.remove();
            }
            if (request.getRequestURL().indexOf(thumbnail_url) == -1) {
                String msg = "请求耗时:" + request.getRequestURL() + "," + (System.currentTimeMillis() - applicationTime);
                if (null != request.getAttribute("view_consuming_timer")) {
                    msg += "/" + request.getAttribute("view_consuming_timer");
                }
                logger.info(msg);
            }
        }
    }
}
