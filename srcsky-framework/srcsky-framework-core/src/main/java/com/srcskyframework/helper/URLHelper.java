package com.srcskyframework.helper;

import java.net.URLEncoder;
import java.util.regex.Matcher;

/**
 * URLHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-5-下午2:29
 * @email z82422@gmail.com
 * @description URL 辅助工具类
 */
public class URLHelper {

    /**
     * 转码 URL 中的 中文参数
     *
     * @param url
     * @param charset
     * @return
     */
    public static String encodeChineseParams(String url, String charset) {
        StringBuffer b = new StringBuffer();
        try {
            Matcher m = RegexHelper.chineseRegex.matcher(url);
            while (m.find()) {
                m.appendReplacement(b, URLEncoder.encode(m.group(0), charset));
            }
            m.appendTail(b);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b.toString();
    }
}
