/**
 * 宝龙电商
 * com.srcskyframework.spring
 * BaseController.java
 *
 * 2013-8-6-下午2:29
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.spring.mvc;

import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.WebHelper;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.HashMap;
import java.util.Map;

/**
 * BaseController
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-6-下午2:29
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class BaseController {

    protected final Logger logger = Logger.getLogger(getClass());

    protected Enterprise input = null;

    @InitBinder
    public void prepare() {
        System.out.println("===========");
        input = WebHelper.getInput();
    }

    protected Map getAjaxData() {
        Map result = new HashMap();
        result.put("success", false);
        result.put("message", "操作提示");
        return result;
    }
}
