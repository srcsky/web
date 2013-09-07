package com.srcskyframework.helper;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:36
 * Email: z82422@gmail.com
 * 验证 对象，使用的 很广泛
 */
public class ValidHelper implements Serializable {

    public static Boolean isNull(Object object) {
        return object == null ? true : false;
    }

    public static Boolean isEmpty(Integer number) {
        return (isNull(number) || number == 0 || number < 1) ? true : false;
    }

    public static Boolean isEmpty(Long number) {
        return (isNull(number) || number == 0 || number < 1) ? true : false;
    }

    public static Boolean isEmpty(String string) {
        return (isNull(string) || string.trim().equalsIgnoreCase("")) ? true : false;
    }

    public static Boolean isEmpty(List list) {
        return (isNull(list) || list.size() < 1) ? true : false;
    }

    public static Boolean isEmpty(Object[] objects) {
        return (isNull(objects) || objects.length < 1) ? true : false;
    }

    public static Boolean isEmpty(Collection objects) {
        return (isNull(objects) || objects.isEmpty() || (objects.size() == 1 && objects.iterator().next() == null)) ? true : false;
    }

    public static Boolean isEmpty(Object object, String... equals) {
        if (isNull(object)) {
            return true;
        } else if (object instanceof String) {
            return isEmpty((String) object) ? true : equals.length == 0 ? false : ((String) object).equalsIgnoreCase(equals[0]);
        } else if (object instanceof Long) {
            return isEmpty((Long) object);
        } else if (object instanceof Collection) {
            return isEmpty((Collection) object);
        } else if (object instanceof Integer) {
            return isEmpty((Integer) object);
        } else if (object instanceof Object[]) {
            return isEmpty((Object[]) object);
        } else if (object instanceof Map) {
            return ((Map) object).isEmpty();
        }
        return equals.length == 0 ? false : object.toString().equalsIgnoreCase(equals[0]);
    }


    public static Boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * <b>功能：</b>验证字符串str是否只包含英文,数字和"-_"
     *
     * @param str 待验证字符串
     * @return true    是只包含英文,数字和"-_" false	不是只包含英文,数字和"-_"
     */
    public static boolean onlyCharAndNumber(String str) {
        return RegexHelper.onlyCharAndNumberRegex.matcher(str).find();
    }


    /**
     * <b>功能：</b>验证字符串str是否只包含英文,数字,中文,空格和"-_"
     *
     * @param str 待验证字符串
     * @return true    是只包含英文,数字,中文和"-_" false	不是只包含英文,数字,中文和"-_"
     */

    public static boolean onlyCharAndNumberAndCh(String str) {
        return RegexHelper.onlyCharAndNumberAndChRegex.matcher(str).find();
    }

    /**
     * <b>功能：</b>验证字符串email是否符合email格式
     *
     * @param email 待验证邮件地址
     * @return true    符合 false	不符合
     */
    public static boolean isEmail(String email) {
        return isEmpty(email) ? false : RegexHelper.emailRegex.matcher(email).find();
    }

    /**
     * <b>功能：</b>验证字符串url是否符合url格式
     *
     * @param url 待验证URL地址
     * @return true    符合 false	不符合
     */
    public static boolean isUrl(String url) {
        return isEmpty(url) ? false : RegexHelper.urlRegex.matcher(url).find();
    }


    /**
     * <b>功能：</b>验证字串是否手机号
     *
     * @param mobile 待验证手机号
     * @return true    符合 false	不符合
     */
    public static boolean isMobile(String mobile) {
        return isEmpty(mobile) ? false : RegexHelper.mobileRegex.matcher(mobile).find();
    }

    /**
     * <b>功能：</b>验证字串图片文件是否属于限定的某中类型格式
     *
     * @param filepath 待验证图片名称
     * @return true    符合 false	不符合
     */
    public static boolean isImagefile(String filepath) {
        return isEmpty(filepath) ? false : RegexHelper.imagePostfixRegex.matcher(filepath).find();
    }


    /**
     * <b>功能：</b>验证字符串date是否符合Date格式
     *
     * @param date 待验证邮件地址
     * @return true    符合 false	不符合
     */
    public static boolean isDate(Object date) {
        if (date instanceof Date) {
            return true;
        }
        return isEmpty(date) ? false : RegexHelper.dateRegex.matcher(date.toString()).find();
    }

    //"^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$"
    //^((((1[6-9]|[2-9]\d)\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\d|3[01]))|(((1[6-9]|[2-9]\d)\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\d|30))|(((1[6-9]|[2-9]\d)\d{2})-0?2-(0?[1-9]|1\d|2[0-8]))|(((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\d):[0-5]?\d:[0-5]?\d$


    public static boolean isNumber(String number) {
        return isEmpty(number) ? false : RegexHelper.numberRegex.matcher(number).find();
    }

    /**
     * <b>功能：</b>验证字符串tags是否符合 Tags标签 正确格式 (aaa ddd ccc)
     *
     * @param tags
     * @return true|false 符合标签格式 true 否则false
     */
    public static boolean tags(String tags) {
        return isEmpty(tags) ? false : RegexHelper.tagRegex.matcher(tags).find();
    }

    /**
     * <b>功能：</b>查找字符串email的邮箱名称
     *
     * @param email
     * @return 邮箱 用户名
     */
    public static String findEmailName(String email) {
        return isEmpty(email) || email.indexOf("@") == -1 ? "" : email.substring(0, email.indexOf("@"));
    }

    /**
     * 正则验证
     *
     * @param value
     * @param regex
     * @return
     */
    public static boolean matches(Object value, String regex) {
        return isEmpty(value) || isEmpty(regex) ? false : String.valueOf(value).trim().matches(regex);
    }


    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("^\\d{4}(\\/|\\-|年)\\d{1,2}(\\/|\\-|月)\\d{1,2}.?(\\s\\d{1,2}.\\d{1,2}(.\\d{1,2})?)?$");
        System.out.println(pattern.matcher("1987/04/22").find());
    }

}
