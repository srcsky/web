package com.srcskyframework.core;

import com.srcskyframework.core.support.Initial;
import com.srcskyframework.helper.CookieHelper;
import com.srcskyframework.helper.ValidHelper;
import com.srcskyframework.helper.WebHelper;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:14
 * Email: z82422@gmail.com
 * 初始化 Initial 接口，
 * 初始化 com.enterprise.support.utility.ActionContext
 */

public class ActionContextFilter implements Filter {

    private Logger logger = Logger.getLogger(ActionContextFilter.class);
    private ServletContext context = null;
    private String characterEncoding = null;
    private Pattern excludeUrlPattern = Pattern.compile("/static/.*|.*\\.ico");
    private static final String thumbnail_url = "/system/thumbnail";


    public void init(FilterConfig config) throws ServletException {
        try {
            context = config.getServletContext();
            characterEncoding = ValidHelper.isEmpty(config.getInitParameter("characterEncoding")) ? "UTF-8" : config.getInitParameter("characterEncoding");
            if (ValidHelper.isNotEmpty(config.getInitParameter("exclude-url-pattern"))) {
                excludeUrlPattern = Pattern.compile(config.getInitParameter("exclude-url-pattern"));
            }
            /*初始化扩展类*/
            String initClass = Configuration.getProperty("system.init.class");
            if (ValidHelper.isNotEmpty(initClass)) {
                Object initObject = Class.forName(initClass).newInstance();
                if (initObject instanceof Initial) {
                    ((Initial) initObject).init(context);
                }
            }

            /*设置Web Root 路径*/
            Constants.WEB_CONTENT_PATH = context.getRealPath("/");

            /*修改保存路径*/
            if (!Configuration.get().isEmpty("web_fileuploader_directory")
                    && Configuration.get().equals("web_fileuploader_absolute", "false")) {
                Configuration.get().set("web_fileuploader_tempdirectory", new File(context.getRealPath("/static/fileuploader/temp")).getAbsolutePath());
                Configuration.get().set("web_fileuploader_directory", new File(context.getRealPath("/static/fileuploader")).getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String url = requestURI.substring(contextPath.length());
        if (null != excludeUrlPattern && excludeUrlPattern.matcher(url).find()) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            /*耗时,记录*/
            final long applicationTime = System.currentTimeMillis();
            /*设置请求编码格式*/
            request.setCharacterEncoding(characterEncoding);
            //init ActionContext:
            ActionContext.setContext(request, response, context);
            Enterprise input = WebHelper.getInput(request);
            if (url.indexOf("enterprise-debug") != -1) {
                if (url.indexOf("enterprise-debug-console") != -1) {
                    /*输出 控制界面*/
                    /*ConsoleAction console = new ConsoleAction();
                    console.proccess();*/
                } else {
                    /*输出 系统信息，环境变量*/
                    response.setContentType("text/html;charset=UTF-8");
                    Enterprise cookies = CookieHelper.getCookies();
                    response.getWriter().println("<pre>" + input + "</pre>");
                    response.getWriter().println("<pre>" + cookies + "</pre>");
                }
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
            ActionContext.remove();
            if (request.getRequestURL().indexOf(thumbnail_url) == -1) {
                String msg = "请求耗时:" + request.getRequestURL() + "," + (System.currentTimeMillis() - applicationTime);
                if (null != request.getAttribute("view_consuming_timer")) {
                    msg += "/" + request.getAttribute("view_consuming_timer");
                }
                logger.info(msg);
            }
        }
    }

    public void destroy() {
        //Stop H2 Server
        //H2ServerManager.stopServer();
    }
}
