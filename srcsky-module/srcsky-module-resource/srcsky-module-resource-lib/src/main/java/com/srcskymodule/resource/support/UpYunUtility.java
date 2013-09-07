package com.srcskymodule.resource.support;


import com.srcskyframework.core.Configuration;
import com.srcskyframework.core.Enterprise;
import com.srcskyframework.helper.DateHelper;
import com.srcskyframework.helper.Md5Helper;
import com.srcskyframework.helper.ValidHelper;
import org.apache.log4j.Logger;

import java.util.Calendar;

public class UpYunUtility {
    private static Logger logger = Logger.getLogger(UpYunUtility.class);

    /**
     * 获取又拍云 API 上传 Policy
     *
     * @param space    空间名称
     * @param filesize 允许上传文件大小单位KB
     * @param minute   Policy过期时间单位分钟
     * @return
     */
    public static String getPolicy(String space, String allowFileType, Long filesize, Integer minute) {
        StringBuffer policyBuffer = new StringBuffer("{");
        /*空间*/
        policyBuffer.append("\"bucket\":\"").append(Configuration.get().getString("spring.job.resource.upyun.space." + space)).append("\",");
        /*Policy过期时间*/
        policyBuffer.append("\"expiration\":").append(DateHelper.add(Calendar.MINUTE, minute).getTime() / 1000).append(",");
        /*保存路径*/
        policyBuffer.append("\"save-key\":\"/fileuploader/" + space + "/{year}/{mon}/{filemd5}.{suffix}\"").append(",");
        /*文件类型*/
        if (!ValidHelper.isEmpty(allowFileType)) {
            policyBuffer.append("\"allow-file-type\":\"").append(allowFileType).append("\",");
        }
        /*允许上传文件大小*/
        policyBuffer.append("\"content-length-range\":\"0,").append(filesize * 1024).append("\"");
        policyBuffer.append(",\"notify-url\":\"").append(Configuration.get().getString("spring.job.resource.upyun.notify_url")).append("\"");
        policyBuffer.append("}");
        logger.debug(policyBuffer);
        String policy = null;//new String(Base64.encode(policyBuffer.toString().getBytes()));
        String signature = Configuration.get().getString("spring.job.resource.upyun.space." + space + ".signature");
        logger.debug(signature);
        signature = Md5Helper.getMD5String(policy + "&" + signature);
        return policy + "|" + signature;
    }

    /**
     * 验证 签名
     *
     * @return
     */
    public static boolean isVerify(Enterprise input) {
        if (input.isEmpty("url")) return false;
        String url = input.getString("url");
        try {
            String space = url.substring(14, url.indexOf("/", 3));
            String signature = Configuration.get().getString("spring.job.resource.upyun.space." + space + ".signature");
            signature = input.getString("code") + "&" + input.getString("message") + "&" + input.getString("url") + "&" + signature;
            return input.equals("sign", Md5Helper.getMD5String(signature));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
        /*image-height	|        737
              image-frames	|        1
              code			|        200
              url			|        /fileuploader/img/2012/07/b4a37068072ca199a4f17fc65fba130d.jpg
              sign			|        a034938e848c942ea8c243260cf93892
              message		|        ok
              image-width	|        550
              time			|        1342774322
              image-type		|        JPEG*/
    }

    public static void main(String[] args) {

        StringBuffer policyBuffer = new StringBuffer("{");
        /*空间*/
        policyBuffer.append("\"bucket\":\"").append("meilepai-static").append("\",");
        /*Policy过期时间*/
        policyBuffer.append("\"expiration\":").append(DateHelper.add(Calendar.MINUTE, 86400).getTime() / 1000).append(",");
        /*保存路径*/
        policyBuffer.append("\"save-key\":\"/static/files/{year}-{mon}-{day}-{hour}-{min}-{sec}.{suffix}\"").append(",");
        /*允许上传文件大小*/
        policyBuffer.append("\"content-length-range\":\"0,").append(102400 * 1024).append("\"");
        policyBuffer.append("}");
        logger.debug(policyBuffer);
        String policy = null;// new String(Base64.encode(policyBuffer.toString().getBytes()));
        String signature = "pDsOLMuP4K8nITD7kW5GE/E/LrA=";
        logger.debug(signature);
        signature = Md5Helper.getMD5String(policy + "&" + signature);
        System.out.println(policy + "|" + signature);
    }
}
