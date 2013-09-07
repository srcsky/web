/**
 * 宝龙电商
 * com.srcskymodule.resource.web
 * FileuploadController.java
 *
 * 2013-8-7-下午3:36
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskymodule.resource.web;

import com.srcskyframework.helper.WebHelper;
import com.srcskyframework.spring.mvc.BaseController;
import com.srcskymodule.resource.domain.ResourceEntity;
import com.srcskymodule.resource.logic.IResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * FileuploadController
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-7-下午3:36
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
@Controller
@RequestMapping("system/uploader")
public class UploaderController extends BaseController {

    @Autowired
    private IResource resourceLogic;


    public Object execute() {
        Map result = getAjaxData();
        try {
            List<ResourceEntity> resourceEntityList = resourceLogic.fileuploader();
            ResourceEntity resourceEntity = resourceEntityList.get(0);
            result.put("image", resourceEntity.getIsimage());
            result.put("postfix", resourceEntity.getPostfix());
            result.put("id", resourceEntity.getId());
            result.put("length", resourceEntity.getLength());
            result.put("hash", resourceEntity.getHash());
            result.put("filepath", resourceEntity.get("filepath"));
            result.put("title", resourceEntity.getTitle());
            result.put("status", resourceEntity.getSynchrostatus());

            if (input.equals("result", "ueditor")) {
                WebHelper.writeHtml("{'url':'" + input.getString("filepath") + "','title':'" + input.getString("pictitle") + "','state':'SUCCESS'}");
                return null;
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return result;
    }
}
