/**
 * 宝龙电商
 * com.srcskymodule.resource.web
 * ResourceController.java
 *
 * 2013-8-7-下午3:17
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskymodule.resource.web;

import com.srcskyframework.spring.mvc.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ResourceController
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-7-下午3:17
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
@Controller
@RequestMapping("resource")
public class FrontController extends BaseController {
    @RequestMapping("index")
    public String execute() {
        return null;
    }
}
