/**
 * 宝龙电商
 * com.srcskyframework.support.helper
 * CookieHelper.java
 *
 * 2013-8-4-下午6:59
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.helper;

import com.srcskyframework.core.ActionContext;
import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Enterprise;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Set;

import static java.net.URLEncoder.encode;

/**
 * CookieHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-4-下午6:59
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class CookieHelper {

    private final static Logger logger = Logger.getLogger(CookieHelper.class);

    /**
     * 获取Cookie 信息
     *
     * @param
     * @return
     */
    public static Enterprise getCookies() {
        Enterprise input = new Enterprise();
        Cookie[] cookies = ActionContext.getContext().getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                try {
                    input.set(cookie.getName(), URLDecoder.decode(cookie.getValue(), "UTF-8"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return input;
    }

    /**
     * 设置Cookie 信息
     *
     * @param
     * @return
     */
    public static void setCookies(Enterprise input) {
        HttpServletResponse response = ActionContext.getContext().getResponse();
        if (response != null) {
            for (String key : (Set<String>) input.keySet()) {
                if (!key.equalsIgnoreCase("timeout")) {
                    try {
                        Cookie cookie = new Cookie(key, encode(input.isEmpty(key) ? "" : input.getString(key), "utf-8"));
                        if (!input.isEmpty("timeout")) {
                            cookie.setMaxAge(input.getInt("timeout"));
                        } else {
                            cookie.setMaxAge(Configuration.get().getInt("web_info_in_cookie.timeout"));
                        }
                        cookie.setPath(Configuration.get().getString("web_info_in_cookie.path"));
                        cookie.setDomain("." + Configuration.get().getString("system.domain"));
                        response.addCookie(cookie);
                    } catch (Exception ex) {
                        logger.error("写入Cookie异常,key=" + key + ",完整参数:" + input, ex);
                    }
                }
            }
        }
    }

    /**
     * 清除 Cookies
     */
    public static void clearCookies() {
        HttpServletRequest request = ActionContext.getContext().getRequest();
        HttpServletResponse response = ActionContext.getContext().getResponse();
        //清除 Cookies
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            cookie.setMaxAge(-1);
            response.addCookie(cookie);
        }
    }

}
