package com.srcskyframework.helper;

import com.srcskyframework.core.ActionContext;
import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Constants;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.exception.ViewLogicException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:43
 * Email: z82422@gmail.com
 * Web层辅助对象
 */
public class WebHelper {
    private final static Logger logger = Logger.getLogger(Logger.class);
    public final static String KEY_UPLOADER_PROGRESS = "key_uploader_progress";
    private final static DiskFileItemFactory factory = new DiskFileItemFactory();
    private final static String SCRIPT_TYPE = "<script text=\"text/javascript\">$script$</script>";


    /**
     * 强制 IE 不缓存
     */
    public static void notCache(HttpServletResponse response) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Expires", "0");
        response.setDateHeader("Expires", 0);
    }

    /**
     * 强制 IE 缓存
     */
    public static void Cache(HttpServletResponse response, int minutes) {
        response.setContentType("text/html; charset=utf-8");
        response.setCharacterEncoding("utf-8");
        response.addHeader("Cache-Control", "max-age=" + (1000 * 60 * minutes));
    }

    public static void setContentType(HttpServletResponse response, String contentType) {
        response.setContentType(contentType + "; charset=utf-8");
    }

    public static void setContentType(String contentType) {
        ActionContext.getContext().getResponse().setContentType(contentType + "; charset=utf-8");
    }

    public static void sendRedirect(String location) throws ViewLogicException {
        try {
            ActionContext.getContext().getResponse().sendRedirect(location);
        } catch (Exception ex) {
            throw new ViewLogicException("WebUtility.sendRedirect异常：" + ex.getMessage());
        }
    }

    /**
     * 文件下载
     *
     * @param filename 文件名称
     * @param input    下载内容
     */
    public static void download(String filename, InputStream input) {
        try {
            HttpServletResponse response = ActionContext.getContext().getResponse();
            response.setContentType(Constants.WEB_CONTENT_TYPE_DOWNLOAD + "; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
            OutputStream out = response.getOutputStream();
            FileHelper.write(input, out, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void download(String filename, String content) {
        try {
            HttpServletResponse response = ActionContext.getContext().getResponse();
            response.setContentType(Constants.WEB_CONTENT_TYPE_DOWNLOAD + "; charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");
            OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
            IOUtils.write(content, out);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void writeHtml(String html) {
        try {
            setContentType("text/html");
            ActionContext.getContext().getResponse().getWriter().write(html);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void writeScript(String script) {
        writeHtml(SCRIPT_TYPE.replace("$script$", script));
    }

    /**
     * 是否POST请求
     *
     * @param request
     * @return
     */
    public static boolean isPostRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST");
    }

    public static boolean isPostRequest() {
        HttpServletRequest request = ActionContext.getContext().getRequest();
        return request.getMethod().equalsIgnoreCase("POST");
    }

    /**
     * 是否Get请求
     *
     * @param request
     * @return
     */
    public static boolean isGetRequest(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("GET");
    }

    public static boolean isGetRequest() {
        HttpServletRequest request = ActionContext.getContext().getRequest();
        return request.getMethod().equalsIgnoreCase("GET");
    }


    public static boolean isFullUrl(String link) {
        if (link == null) {
            return false;
        }
        link = link.trim().toLowerCase();
        return link.startsWith("http://") || link.startsWith("https://") || link.startsWith("file://");
    }

    public static String fullUrl(String pageUrl, String link) {
        if (isFullUrl(link)) {
            return link;
        } else if (link != null && link.startsWith("?")) {
            int qindex = pageUrl.indexOf('?');
            int len = pageUrl.length();
            if (qindex < 0) {
                return pageUrl + link;
            } else if (len > qindex + 1) {
                return pageUrl.substring(0, qindex) + link;

            } else if (qindex == len - 1) {
                return pageUrl.substring(0, len - 1) + link;
            } else {
                return pageUrl + "&" + link.substring(1);
            }
        }

        boolean isLinkAbsolute = link.startsWith("/");
        if (!isFullUrl(pageUrl)) {
            pageUrl = "http://" + pageUrl;
        }

        int slashIndex = isLinkAbsolute ? pageUrl.indexOf("/", 8) : pageUrl.lastIndexOf("/");
        if (slashIndex <= 8) {
            pageUrl += "/";
        } else {
            pageUrl = pageUrl.substring(0, slashIndex + 1);
        }

        return isLinkAbsolute ? pageUrl + link.substring(1) : pageUrl + link;
    }


    public static String replacelink(String content, String link) {
        return content.replaceAll("(<a[^<>]+(51cto.com)+[^<>]+>(.+)</a>)", "1");
        //+[^<>]+>(.+)
    }

    public static String replace(String content, String regex, String value) {
        return content.replaceAll(regex, value);
    }


    /**
     * 封装 转换Request 为 Enterprise DATA
     *
     * @return
     */
    public static Enterprise getInput() {
        HttpServletRequest request = ActionContext.getContext().getRequest();
        return getInput(request);
    }


    /**
     * 封装 转换Request 为 Enterprise DATA
     *
     * @param request
     * @return
     */
    public static Enterprise getInput(HttpServletRequest request) {
        if (null != request.getAttribute("input") && request.getAttribute("input") instanceof Enterprise) {
            return (Enterprise) request.getAttribute("input");
        }
        Enterprise entity = new Enterprise("REQUEST_DATA");
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            String[] values = request.getParameterValues(key);
            for (String value : values) {
                if (entity.containsKey(key) && !key.equalsIgnoreCase("id")) {
                    Object temp = entity.get(key);
                    if (temp instanceof List) {
                        ((List) temp).add(value);
                    } else {
                        List temps = new ArrayList();
                        temps.add(temp);
                        temps.add(value);
                        entity.put(key, temps);
                    }
                } else {
                    entity.put(key, value);
                }
            }
        }
        if (ServletFileUpload.isMultipartContent(request)) {
            //ServletRequestContext servletResultContext = new ServletRequestContext(request);
            try {
                ServletFileUpload fileupload = new ServletFileUpload(factory);
                fileupload.setSizeMax(Long.valueOf((1024 * 1024) * Configuration.get().getLong("web_fileuploader_maxsize")));
                fileupload.setHeaderEncoding("UTF-8");
                fileupload.setProgressListener(new ProgressListener() {
                    public void update(long read, long length, int i) {
                        HttpSession session = ActionContext.getContext().getRequest().getSession();
                        Enterprise progress = (Enterprise) session.getAttribute(KEY_UPLOADER_PROGRESS);
                        if (null == progress) {
                            progress = new Enterprise("UploaderProgress");
                            session.setAttribute(KEY_UPLOADER_PROGRESS, progress);
                        }
                        progress.set("read", read);
                        progress.set("length", length);
                        BigDecimal readDecimal = new BigDecimal(read);
                        BigDecimal lengthDecimal = new BigDecimal(length);
                        BigDecimal percent = new BigDecimal(100);
                        progress.set("percent", length == read ? 100 : readDecimal.divide(lengthDecimal, 4, BigDecimal.ROUND_HALF_UP).multiply(percent).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                });
                List<FileItem> fileItem = fileupload.parseRequest(request);
                for (FileItem field : fileItem) {
                    String value = field.isFormField() ? field.getString("UTF-8") : "";
                    if (!field.isFormField() || !ValidHelper.isEmpty(value))
                        if (entity.containsKey(field.getFieldName())) {
                            Object temp = entity.get(field.getFieldName());
                            if (temp instanceof List) {
                                ((List) temp).add(field.isFormField() ? value : field);
                            } else {
                                List temps = new ArrayList();
                                temps.add(temp);
                                temps.add(field.isFormField() ? value : field);
                                entity.put(field.getFieldName(), temps);
                            }
                        } else {
                            entity.put(field.getFieldName(), field.isFormField() ? value : field);
                        }
                }
            } catch (Exception ex) {
                System.err.println("上传文件读取内容异常:" + ex.getMessage());
                ex.printStackTrace();
            }
        }
        request.setAttribute("input", entity);
        return entity;
    }

    public static String appendURL(Map<String, String> temps, String action) {
        try {
            StringBuffer strBuffer = new StringBuffer();
            Map<String, Object> parms = (ActionContext.getContext() == null) ? Collections.<String, Object>emptyMap() : getInput(ActionContext.getContext().getRequest());
            for (String key : parms.keySet()) {
                if (!temps.containsKey(key) && !key.equalsIgnoreCase("enterprise_page_index")) {
                    if (!ValidHelper.isEmpty(strBuffer.toString())) {
                        strBuffer.append("&");
                    }
                    strBuffer.append(key).append("=").append(URLEncoder.encode(parms.get(key).toString(), "utf-8"));
                }
            }
            if (!temps.keySet().isEmpty()) {
                for (String key : temps.keySet()) {
                    if (!ValidHelper.isEmpty(strBuffer.toString())) {
                        strBuffer.append("&");
                    }
                    strBuffer.append(key).append("=").append(URLEncoder.encode(temps.get(key).toString(), "utf-8"));
                }
            }
            if (!ValidHelper.isEmpty(strBuffer)) {
                return action + "?" + strBuffer.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return action;
    }


    /**
     * 获取绝对路径
     *
     * @param path
     */
    public static String getAbsolutePath(String path) {
        HttpServletRequest request = ActionContext.getContext().getRequest();
        StringBuffer absolutePath = new StringBuffer();
        absolutePath.append(request.getContextPath()).append(path);
        return absolutePath.toString();

    }

    /**
     * URL 编码
     *
     * @return
     */
    public static String getURLEncoder(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    /**
     * URL 解码
     *
     * @param url
     * @return
     */
    public static String getURLDecoder(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return url;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
