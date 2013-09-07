package com.srcskyframework.helper;


import com.srcskyframework.cas.Authorization;
import com.srcskyframework.core.ActionContext;
import com.srcskyframework.core.Constants;
import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:36
 * Email: z82422@gmail.com
 * 会话 操作辅助对象
 */
public class SessionHelper {

    private static Logger logger = Logger.getLogger(SessionHelper.class);

    /**
     * 获取授权信息
     * SSO 登录陈宫
     *
     * @return
     */
    public static Authorization getAuthorization() {
        return ActionContext.getContext().getSessionAttr(Constants.WEB_SESSION_CAS_AUTHORIZATION);
    }

    public void setAuthorization(Authorization authorization) {
        ActionContext.getContext().setSessionAttr(Constants.WEB_SESSION_CAS_AUTHORIZATION, authorization);
    }
}
