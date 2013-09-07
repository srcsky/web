package com.srcskyframework.helper;

import com.srcskyframework.core.ActionContext;
import com.srcskyframework.core.Enterprise;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午9:26
 * Email: z82422@gmail.com
 * 分页对象  依赖与 WebUtility,ActionContext 等对象
 */

public class Pagination<T extends Enterprise> implements Serializable {

    /*页大小*/
    public final static String PAGE_SIZE = "enterprise_page_size";
    /*总页数*/
    public final static String PAGE_COUNT = "enterprise_page_count";
    /*总记录数*/
    public final static String PAGE_RESULT_COUNT = "enterprise_page_result_count";
    /*当前索引页数*/
    public final static String PAGE_INDEX = "enterprise_page_index";
    /*结果*/
    public final static String PAGE_RESULT = "enterprise_page_result";
    /*显示几页*/
    public final static String PAGE_INDEX_COUNT = "enterprise_page_index_count";
    /*排序*/
    public final static String PAGE_ORDER_BY = "enterprise_page_order_by";

    /*首页文字*/
    public final static String PAGE_SHOW_FIRST_TEXT = "page_show_first_text";
    /*末页文字*/
    public final static String PAGE_SHOW_LAST_TEXT = "page_show_last_text";
    /*上一页文字*/
    public final static String PAGE_SHOW_PARENT_TEXT = "page_show_parent_text";
    /*下一页文字*/
    public final static String PAGE_SHOW_NEXT_TEXT = "page_show_next_text";
    /*当前页位置*/
    public final static String PAGE_SHOW_CURERENT_POSITION = "page_show_current_position";
    /*显示多少条记录*/
    public final static String PAGE_SHOW_RESULT_COUNT = "page_show_result_count";
    /*显示多少页记录*/
    public static String PAGE_SHOW_COUNT = "page_show_count";

    public static String PAGE_CACHE_TIMER = "page_cahce_timer";

    public final static String PAGE_SHOW_AJAX_CONTEXT_EXPR = "page_show_ajax_context_expr";

    /*Submit 函数*/
    public final static String PAGE_SHOW_SUBMIT_CALLBACK = "page_show_submit_callback";


    private Enterprise enterprise;

    public Pagination(Enterprise enterprise) {
        if (!enterprise.containsKey(PAGE_COUNT)) {
            enterprise.set(PAGE_COUNT, 0);
        }
        if (!enterprise.containsKey(PAGE_INDEX)) {
            enterprise.set(PAGE_INDEX, 1);
        }
        if (!enterprise.containsKey(PAGE_RESULT_COUNT)) {
            enterprise.set(PAGE_RESULT_COUNT, 0);
        }
        if (!enterprise.containsKey(PAGE_SIZE)) {
            enterprise.set(PAGE_SIZE, 20);
        }
        if (!enterprise.containsKey(PAGE_RESULT)) {
            enterprise.set(PAGE_RESULT, Collections.emptyList());
        }
        if (!enterprise.containsKey(PAGE_INDEX_COUNT)) {
            enterprise.set(PAGE_INDEX_COUNT, 8);
        }
        if (!enterprise.containsKey(PAGE_INDEX_COUNT)) {
            enterprise.set(PAGE_INDEX_COUNT, 8);
        }
        if (!enterprise.containsKey(PAGE_SHOW_FIRST_TEXT)) {
            enterprise.set(PAGE_SHOW_FIRST_TEXT, "&laquo;&laquo;启始页");
        }
        if (!enterprise.containsKey(PAGE_SHOW_LAST_TEXT)) {
            enterprise.set(PAGE_SHOW_LAST_TEXT, "末尾页&raquo;&raquo;");
        }
        if (!enterprise.containsKey(PAGE_SHOW_PARENT_TEXT)) {
            enterprise.set(PAGE_SHOW_PARENT_TEXT, "&laquo;上一页");
        }
        if (!enterprise.containsKey(PAGE_SHOW_NEXT_TEXT)) {
            enterprise.set(PAGE_SHOW_NEXT_TEXT, "下一页&raquo;");
        }
        if (!enterprise.containsKey(PAGE_SHOW_COUNT)) {
            enterprise.set(PAGE_SHOW_COUNT, true);
        }
        if (!enterprise.containsKey(PAGE_SHOW_CURERENT_POSITION)) {
            enterprise.set(PAGE_SHOW_CURERENT_POSITION, "2");
        }
        if (!enterprise.containsKey(PAGE_SHOW_AJAX_CONTEXT_EXPR)) {
            enterprise.set(PAGE_SHOW_AJAX_CONTEXT_EXPR, ".pager a");
        }
        if (!enterprise.containsKey(PAGE_SHOW_RESULT_COUNT)) {
            enterprise.set(PAGE_SHOW_RESULT_COUNT, true);
        }
        if (!enterprise.containsKey(PAGE_CACHE_TIMER)) {
            enterprise.set(PAGE_CACHE_TIMER, 0);
        }
        if (!enterprise.containsKey(PAGE_SHOW_SUBMIT_CALLBACK)) {
            enterprise.set(PAGE_SHOW_SUBMIT_CALLBACK, "window.gotoPager");
        }
        this.enterprise = enterprise;
    }

    public Enterprise getEnterprise() {
        return this.enterprise;
    }

    /**
     * 分页结果数据
     *
     * @return
     */
    public List<T> getResults() {
        return (List) enterprise.get(PAGE_RESULT);
    }

    public int getResultcount() {
        return enterprise.getInt(PAGE_RESULT_COUNT);
    }

    public int getCount() {
        if (getResultcount() % getSize() == 0) {
            return getResultcount() / getSize();
        } else {
            return (getResultcount() + getSize() - 1) / getSize();
        }
    }

    public int getIndex() {
        return enterprise.getInt(PAGE_INDEX) == 0 ? 1 : enterprise.getInt(PAGE_INDEX);
    }

    public int getSize() {
        return enterprise.getInt(PAGE_SIZE);
    }

    public int getIndexCount() {
        return enterprise.getInt(PAGE_INDEX_COUNT);
    }

    /**
     * 是否有上一页
     *
     * @return
     */
    public boolean hasPrevs() {
        return getIndex() - 1 > 0;
    }

    /**
     * 是否有下一页
     *
     * @return
     */
    public boolean hasNext() {
        return getIndex() + 1 <= getCount();
    }

    /**
     * 输出分页 Html
     *
     * @return
     */
    public final String toString() {
        if (getResultcount() == 0) {
            return "<div class=\"pager\">抱歉,暂无数据...</div>";
        }
        StringBuffer htmlBuffer = new StringBuffer();
        String action = enterprise.isEmpty("action") ? "" : enterprise.getString("action");
        String method = enterprise.isEmpty("method") ? "get" : enterprise.getString("method");
        String context = enterprise.isEmpty("context") ? "" : enterprise.getString("context");

        String tempAction = action;
        String click = "";
        if (method.equalsIgnoreCase("post")) {
            //Post Method
            tempAction = "javascript:void(0);";
        } else if (method.equalsIgnoreCase("ajax")) {
            //Ajax Method
            if (tempAction.indexOf("?") != -1) {
                tempAction = tempAction.substring(0, tempAction.indexOf("?"));
            }
            htmlBuffer.append("\n<script type=\"text/javascript\">var params={};");
            Map<String, Object> parms = (ActionContext.getContext() == null) ? Collections.<String, Object>emptyMap() : WebHelper.getInput(ActionContext.getContext().getRequest());
            for (String key : parms.keySet()) {
                htmlBuffer.append("params[\"" + key + "\"]=\"" + parms.get(key) + "\";");
            }
            htmlBuffer.append("$(function(){$(\"" + enterprise.getString(PAGE_SHOW_AJAX_CONTEXT_EXPR) + "\").click(function(){params[\"enterprise_page_index\"]=this.id;");
            //$.ajax({url: "jsp/top_ajax.jsp",beforeSend : function() {
            htmlBuffer.append("$.ajax({url:\"" + tempAction + "\",data:params,cache:false,beforeSend:function(){$(\"" + context + "\").parent().find(\".mask_top\").show()},complete:function($data){$(\"" + context + "\").html($data.responseText);$(\"" + context + "\").parent().find(\".mask_top\").hide();}});");
            //htmlBuffer.append("$.ajax({url:\"" + tempAction + "\",data:params,function($data){.html($data);});");
            htmlBuffer.append("return false;});});");
            htmlBuffer.append("</script>");
            tempAction = "javascript:void(0);";
        } else if (method.equalsIgnoreCase("submit")) {
            /*htmlBuffer.append("\n<script type=\"text/javascript\">var params={};");
            htmlBuffer.append("$(function(){$(\"" + enterprise.getString(PAGE_SHOW_AJAX_CONTEXT_EXPR) + "\").click(function(){");
            htmlBuffer.append(enterprise.getString(PAGE_SHOW_SUBMIT_CALLBACK) + "($(this).attr(\"id\"));");
            htmlBuffer.append("return false;});});");
            htmlBuffer.append("</script>");*/
            tempAction = "javascript:void(0);";
            click = enterprise.getString(PAGE_SHOW_SUBMIT_CALLBACK) + "({%1})";
        } else {
            Map<String, Object> parms = (ActionContext.getContext() == null) ? Collections.<String, Object>emptyMap() : WebHelper.getInput();
            //Get Method
            if (!ValidHelper.isEmpty(tempAction)) {
                for (String key : parms.keySet()) {
                    if (tempAction.indexOf(key) != -1 && !key.equalsIgnoreCase("index") && !key.equalsIgnoreCase("enterprise_page_index")) {
                        tempAction = tempAction.replace("_" + key, parms.get(key).toString());
                    }
                }
            } else {
                Map temps = new HashMap();
                if (!parms.containsKey("index")) {
                    temps.put("enterprise_page_index", "{%1}");
                } else {
                    temps.put("index", "{%1}");
                }
                tempAction = WebHelper.appendURL(temps, tempAction);
            }
        }

        int index = getIndex();
        int indexCount = getIndexCount();
        int count = getCount();
        if (index > count) {
            index = count;
        }

        int startIndex = 1;
        if (index - enterprise.getInt(PAGE_SHOW_CURERENT_POSITION) > startIndex) {
            startIndex = index - enterprise.getInt(PAGE_SHOW_CURERENT_POSITION);
        }

        int endIndex = count;
        if (endIndex > startIndex + indexCount) {
            endIndex = startIndex + indexCount;
        }
        if (startIndex > endIndex - indexCount) {
            startIndex = endIndex - indexCount;
        }

        /*生成分页页代码*/
        htmlBuffer.append("<div class=\"pager\">");
        String url = "<a href=\"" + tempAction + "\" id=\"{%1}\" class=\"{%3}\" " + (click.equals("") ? "" : " onClick=\"" + click + "\"") + ">{%2}</a>";
        if (index > 1) {
            if (index - 1 > 0) {
                if (index > 3 && !enterprise.isEmpty(PAGE_SHOW_FIRST_TEXT)) {
                    htmlBuffer.append(url.replace("{%1}", "1").replace("{%3}", "start").replace("{%2}", enterprise.getString(PAGE_SHOW_FIRST_TEXT)));
                }
                htmlBuffer.append(url.replace("{%1}", String.valueOf(index - 1)).replace("{%3}", "up").replace("{%2}", enterprise.getString(PAGE_SHOW_PARENT_TEXT)));
            } else {
                htmlBuffer.append(url.replace("{%1}", "1").replace("{%3}", "up").replace("{%2}", enterprise.getString(PAGE_SHOW_PARENT_TEXT)));
            }
        } else {
            htmlBuffer.append("<span class=\"disabled up\">" + enterprise.getString(PAGE_SHOW_PARENT_TEXT) + "</span>");
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (index != i) {
                if (i > 0) {
                    htmlBuffer.append(url.replace("{%1}", String.valueOf(i)).replace("{%2}", String.valueOf(i)));
                }
            } else {
                htmlBuffer.append("<span class=\"current\" title=\"本页\">{%2}</span>".replace("{%2}", String.valueOf(i)));
            }
        }

        if (count > index) {
            if (index + 1 > count) {
                htmlBuffer.append("<span class=\"disabled down\">" + enterprise.getString(PAGE_SHOW_NEXT_TEXT) + "</span>");
            } else {
                htmlBuffer.append(url.replace("{%1}", String.valueOf(index + 1)).replace("{%3}", "down").replace("{%2}", enterprise.getString(PAGE_SHOW_NEXT_TEXT)));
                if (count > indexCount + 1 && !enterprise.isEmpty(PAGE_SHOW_LAST_TEXT)) {
                    htmlBuffer.append(url.replace("{%1}", String.valueOf(count)).replace("{%3}", "end").replace("{%2}", enterprise.getString(PAGE_SHOW_LAST_TEXT)));
                }
            }
        } else {
            htmlBuffer.append("<span class=\"disabled\">" + enterprise.getString(PAGE_SHOW_NEXT_TEXT) + "</span>");
        }

        if (!enterprise.isEmpty(PAGE_SHOW_COUNT) && enterprise.getBoolean(PAGE_SHOW_COUNT)) {
            htmlBuffer.append("<span class=\"disabled\">&nbsp;共" + count + "页&nbsp;</span>");
        }
        if (!enterprise.isEmpty(PAGE_SHOW_RESULT_COUNT) && enterprise.getBoolean(PAGE_SHOW_RESULT_COUNT)) {
            htmlBuffer.append("<span class=\"disabled\">&nbsp;共" + getResultcount() + "条&nbsp;</span>");
        }
        htmlBuffer.append("</div>\n");

        return htmlBuffer.toString();
    }

    public Pagination put(String key, String value) {
        enterprise.put(key, value);
        return this;
    }

    public static String getOrderByField() {
        HttpSession session = ActionContext.getContext().getSession(false);
        Enterprise input = WebHelper.getInput();
        if (null != session && (!(input.isEmpty("enterprise_page_order_by"))) || (!(input.isEmpty("enterprise_page_order_by_new")))) {
            String orderby_request = (!(input.isEmpty("enterprise_page_order_by"))) ? input.getString("enterprise_page_order_by") : input.getString("enterprise_page_order_by_new");
            String ordersort = (null != session.getAttribute("enterprise_page_order_by_SORT")) ? (String) session.getAttribute("enterprise_page_order_by_SORT") : "asc";
            session.setAttribute("enterprise_page_order_by", orderby_request);
            if (!(input.isEmpty("enterprise_page_order_by_new")))
                session.setAttribute("enterprise_page_order_by_SORT", ((ordersort == null) || (ordersort.equalsIgnoreCase("asc"))) ? "desc" : "asc");

            input.set("enterprise_page_order_by", orderby_request);
            input.remove("enterprise_page_order_by_new");
            return "simpleName." + input.getString("enterprise_page_order_by") + " " + ordersort;
        }
        return null;
    }
}