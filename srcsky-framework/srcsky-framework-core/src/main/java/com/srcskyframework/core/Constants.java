package com.srcskyframework.core;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-11-16
 * Time: 上午10:43
 * Email: z82422@gmail.com
 * 通用 常量类
 */
public class Constants {

    /**
     * Tools
     */
    /*================== 工具 =============================================================================================*/
    public final static String LINE = "=====================================================================";
    public final static String LINE_HALF = "=================================";

    /**
     * 逻辑常量
     */
    /*================== YES,NO =============================================================================================*/
    public final static Integer YES = 1;
    public final static Integer NO = 2;

    /**
     * Web层
     */
    /*================== Web Session 中常量：当前登录用户，当前登录管理员,记住用户选项，验证码 ==================*/
    public final static String WEB_SESSION_MEMBER_KEY = "web_session_member_key";
    public final static String WEB_SESSION_ADMIN_KEY = "web_session_admin_key";
    public final static String WEB_SESSION_MERCHANT_KEY = "web_session_merchant_key";
    public final static String WEB_SESSION_UNION_KEY = "web_session_union_key";
    public final static String WEB_SESSION_COOKIE_REMEMBER_USER_KEY = "web_session_cookie_remember_user_key";
    public final static String WEB_SESSION_VERIFY_NUMBER = "web_session_verify_number";
    public final static String WEB_SESSION_CONNECT_LOGIN = "web_session_connect_key";
    public final static String WEB_SESSION_ADMIN_ARTICLE_TEMP_PHOTO_KEY = "web_session_admin_article_temp_photo_key";
    public final static String WEB_SESSION_ADMIN_ARTICLE_TEMP_PHOTO_COVER_KEY = "web_session_admin_article_temp_photo_cover_key";
    public final static String WEB_SESSION_CITY_KEY = "web_session_city_key";
    // 授权信息
    public final static String WEB_SESSION_CAS_AUTHORIZATION = "web_session_cas_authorization";

    /*================== Web Request 中常量：当前所在城市 ==================*/
    public final static String WEB_REQUEST_CITY_KEY = "web_request_city_key";
    /*================== Web ContentType==================*/
    public final static String WEB_CONTENT_TYPE_DOWNLOAD = "application/octet-stream";
    public final static String WEB_CONTENT_TYPE_WORD = "application/msword";
    /*================== 应用路径*/
    public static String WEB_ROOT_ABSOLUTE_PATH = "";
    public static String WEB_CONTENT_PATH = "";


    /**
     * 业务审核常量
     */
    /*================== 审核状态：未开始审核(草稿)/开始审核(定稿)/待定/二次审核/审核通过/审核不通过==================*/
    /*================== 第一种：INIT->SUCCESS 或 FIAL */
    /*================== 第二种：INIT->SECOND  或 SUCCESS  或 FIAL  */
    /*================== 第三种：INIT->START-> SUCCESS  或 FIAL  */
    public final static Integer AUDITING_STATUS_INIT = 1;
    public final static Integer AUDITING_STATUS_START = 2;
    public final static Integer AUDITING_STATUS_PENDING = 3;
    public final static Integer AUDITING_STATUS_SECOND = 4;
    public final static Integer AUDITING_STATUS_SUCCESS = 5;
    public final static Integer AUDITING_STATUS_SUCCESS_SECOND = 55;
    public final static Integer AUDITING_STATUS_FAIL = 6;

    /**
     * 男女常量
     */
    /*================== 女，男=======================================================================================*/
    public final static Integer USER_SEX_WOMAN = 1;
    public final static Integer USER_SEX_MAN = 2;

    /**
     * 用户类型
     */
    /*================== 普通用户,商家,管理员=========================================================================*/
    public final static Integer USER_TYPE_USER = 1;
    public final static Integer USER_TYPE_MERCHANT = 2;
    public final static Integer USER_TYPE_ADMIN = 3;

    /**
     * 用户信息 来源 定义
     */
    /*================== 后台添加，通过网站注册，采集生成，联合登录来的=========================================================*/
    public final static Integer USER_ORGIN_ADMIN_CONSOLE_ADD = 9;
    public final static Integer USER_ORGIN_WEB_REGISTER = 1;
    public final static Integer USER_ORGIN_FETCH_GENERATE = 2;
    public final static Integer USER_ORGIN_CONNECT_QQ = 5;
    public final static Integer USER_ORGIN_CONNECT_SINA = 6;
    public final static Integer USER_ORGIN_CONNECT_DOUBAN = 7;
    public final static Integer USER_ORGIN_CONNECT_RENREN = 8;

    /**
     * 状态 定义
     */
    /*================== 启用，禁用，删除=============================================================================*/
    public final static Integer STATUS_ENABLE = 1, STATUS_SUCCESS = 1;
    public final static Integer STATUS_DISABLED = 2, STATUS_FINAL = 2;
    public final static Integer STATUS_DELETE = 3;
    /*================== 处理状态=====================================================================================*/
    public final static Integer STATUS_PROCCESS_INIT = 1;/*未处理*/
    public final static Integer STATUS_PROCCESS_IN = 2;/*处理中*/
    public final static Integer STATUS_PROCCESS_SUCCESS = 3;/*处理完成*/
    public final static Integer STATUS_PROCCESS_FAIL = 4;/*处理失败*/


    /**
     * 资源
     */
    /*================== 来路 ========================================================================================*/
    public final static Integer RESOURCE_ORIGIN_UPLOADER = 1;
    public final static Integer RESOURCE_ORIGIN_UPUN = 2;
    /*================== 用户,产品====================================================================================*/
    public final static Integer RESOURCE_USER_HEAD = 1;/*头像*/

    /**
     * 资源分类
     */
    /*================== 店铺介绍图片，店铺团队图片，店铺LOGO，店铺影集/相册,店铺影集/相册(封面) =====================*/
    public final static Integer RESOURCE_MERCHANT_LOGO = 1001;/*Logo,标志*/
    public final static Integer RESOURCE_MERCHANT_PHOTO = 1002;/*商户图集*/
    public final static Integer RESOURCE_MERCHANT_AUTH = 1003;/*商家资质文件*/
    public final static Integer RESOURCE_MERCHANT_CLAIM = 1004;/*商家认领凭证*/
    public final static Integer RESOURCE_MERCHANT_ALBUM_PHOTO = 1010;/*影集图片*/
    public final static Integer RESOURCE_MERCHANT_PRODUCT_COVER = 1020;/*产品封面*/
    public final static Integer RESOURCE_AD = 1030;/*广告图片*/
    public final static Integer RESOURCE_AD_COVER = 1031;/*广告封面*/
    public final static Integer RESOURCE_LINK_LOGO = 1040;/*友情链接LOGO*/

    /**
     * 数量统计
     */
    /*================== 点击数,浏览数,评论数,好评数，坏评数,销售量,收藏数============================================*/
    public final static Long HIT_COUNT_CLICK = 1l;
    public final static Long HIT_COUNT_BROWSE = 2l;
    public final static Long HIT_COUNT_COMMENT = 3l;
    public final static Long HIT_COUNT_BAD = 4l;
    public final static Long HIT_COUNT_GOOD = 5l;
    public final static Long HIT_COUNT_SALES = 6l;
    public final static Long HIT_COUNT_FAVORITES = 7l;


    /**
     * 认证分类，
     */
    /*================== 邮箱验证,密码重设，绑定手机,VIP券发送========================================================*/
    public final static String VERIFICATION_EMAIL_CHECK = "verification_email_check";
    public final static String VERIFICATION_PASSWORD_RESET = "verification_password_reset";
    public final static String VERIFICATION_MOBILE_BIND = "verification_mobile_bind";

    /**
     * 消息模板分类
     */
    /*================== 邮箱激活,重设密码,意见反馈，短信绑定,公告====================================================*/
    public final static String TEMPLATE_CATEGORY_EMAIL_ACTIVE = "template_category_email_active";
    public final static String TEMPLATE_CATEGORY_EMAIL_PASSWORD_RESET = "template_category_email_password_reset";
    public final static String TEMPLATE_CATEGORY_EMAIL_FEEDBACK = "template_category_email_feedback";
    public final static String TEMPLATE_CATEGORY_SMS_BIND = "template_category_sms_bind";

    /**
     * 评论类型
     */
    /*================== 主题,评论/消息,回复,已发=====================================================================*/
    public final static Long COMMENT_CATEGORY_TOPIC = 1l;
    public final static Long COMMENT_CATEGORY_COMMENT = 2l;
    public final static Long COMMENT_CATEGORY_REPLY = 3l;
    public final static Long COMMENT_CATEGORY_ALREADY_SEND = 4l;

    /**
     * 文章类型
     */
    /*==================LINK文章(关于我们，帮助中心，招贤纳士),公告文章===============================================*/
    public final static Integer ARTICLE_CATEGORY_LINK = 1;
    public final static Integer ARTICLE_CATEGORY_NOTICE = 2;


    /**
     * 广告类型
     */
    /*================== 广告类型，商家右侧广告=======================================================================*/
    public final static Integer AD_CATEGORY_HOME_BANNER = 1;
    public final static Integer AD_CATEGORY_MERCHANT_RIGHT = 2;


    /**
     * 权限规则 分类
     */
    /*================== 路径匹配规则,SEL匹配规则,LOGIC(硬编码逻辑权限)===============================================*/
    public final static Integer SECURITY_RESOURCE_TYPE_PATH = 1;
    public final static Integer SECURITY_RESOURCE_TYPE_EXPRESSION = 2;
    public final static Integer SECURITY_RESOURCE_TYPE_LOGIC = 3;


    /**
     * 验证常量
     */
    /*================== DAO开启 修改验证==================*/
    public final static String VALIDATION_BY_DAO_UPDATE = "validation_by_dao_update_";

    /**
     * Log日志 分类
     */
    /*================== 添加记录，删除记录,修改记录，登录 ===========================================================*/
    public final static Integer LOG_TYPE_ADD = 1001;
    public final static Integer LOG_TYPE_DROP = 1002;
    public final static Integer LOG_TYPE_UPDATE = 1003;
    public final static Integer LOG_TYPE_LOGIN = 1004;

    /**
     * 用户消息分类
     */
    /*================== 用户公告，商家公告 ===========================================================*/
    public final static String MESSAGE_USER_NOTICE = "message_user_notice";
    public final static String MESSAGE_MERCHANT_NOTICE = "message_merchant_notice";

}
