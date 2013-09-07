/**
 * 宝龙电商
 * com.srcskyframework.core.helper
 * RegexHelper.java
 *
 * 2013-8-5-下午2:27
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.helper;

import java.util.regex.Pattern;

/**
 * RegexHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-5-下午2:27
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class RegexHelper {
    // 中文
    public static Pattern chineseRegex = Pattern.compile("[\u4e00-\u9fa5]+");
    // 数字
    public static final Pattern numberRegex = Pattern.compile("^[0-9]+$");
    // 包含数字以及横线
    public static final Pattern onlyCharAndNumberRegex = Pattern.compile("^[a-zA-Z0-9]+([-_][a-zA-Z0-9]+)*$");
    // 同上在加中文
    public static final Pattern onlyCharAndNumberAndChRegex = Pattern.compile("^[a-zA-Z0-9\u4e00-\u9fa5\\s]+([-_][a-zA-Z0-9\u4e00-\u9fa5\\s]+)*$");
    // 邮箱
    public static final Pattern emailRegex = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.[a-z]+([-.][a-z]+)*$");
    // 手机号
    public static final Pattern mobileRegex = Pattern.compile("^1\\d{10}$", Pattern.CASE_INSENSITIVE);
    // 图片后缀
    public static final Pattern imagePostfixRegex = Pattern.compile("\\.(?:GIF|JPG|JPEG|BMP|PNG)$", Pattern.CASE_INSENSITIVE);
    // 日期
    public static final Pattern dateRegex = Pattern.compile("^\\d{4}(\\/|\\-|年)\\d{1,2}(\\/|\\-|月)\\d{1,2}.?(\\s\\d{1,2}.\\d{1,2}(.\\d{1,2})?)?$");
    // 日期
    public static final Pattern dateRegexSimple = Pattern.compile("^\\d{4}.\\d{1,2}.\\d{1,2}.?$");
    // 网址
    public static final Pattern urlRegex = Pattern.compile("^http:\\/\\/([\\w-]+(\\.[\\w-]+)+)+$");
    // 标签
    public static final Pattern tagRegex = Pattern.compile("^\\s*(([a-z0-9A-Z\\u4e00-\\u9fa5]{2,8})\\s+){0,2}([a-z0-9A-Z\\u4e00-\\u9fa5]{2,8}\\s*)?$");

}
