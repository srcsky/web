package com.srcskyframework.core.support;


import javax.servlet.ServletContext;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:13
 * Email: z82422@gmail.com
 * 系统初始化时 预留的 扩展接口，
 * 例如： 系统初始化需要 加载所有城市信息到 内存，或者分类信息到内存。
 * 就应实现该接口 配置在 web.xml文件中
 * <p/>
 * 该接口 是 ActionContextFilter 的一个补充
 */
public interface Initial {
    void init(ServletContext servletContext);
}
