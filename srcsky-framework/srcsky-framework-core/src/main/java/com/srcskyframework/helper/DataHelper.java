package com.srcskyframework.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * DataHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-5-下午2:18
 * @email zhanggj-hws@powerlong.com
 * @description z82422@gmail.com
 */
public class DataHelper {
    /**
     * 强制将String类型转换为Long类型;
     *
     * @param size
     * @return
     */
    public static Long getByteSize(String size) {
        String nuit = size.substring(size.length() - 1, size.length()).toUpperCase();
        if (nuit.equalsIgnoreCase("K")) {
            String num = size.substring(0, size.length() - 1);
            return Long.parseLong(num) * 1024;
        } else if (nuit.equalsIgnoreCase("M")) {
            String num = size.substring(0, size.length() - 1);
            return Long.parseLong(num) * 1024 * 1024;
        } else {
            return Long.parseLong(size);
        }
    }

    /**
     * 强制将String类型转换为Long类型;
     *
     * @param size
     * @return
     */
    public static String getByteCNM(Long size) {
        if (size == 0 || size < 1024) {
            return "1K";
        } else if (size / (1024) < 1024) {
            return (size / 1024) + "K";
        } else if (size / (1024 * 1024) < 1024) {
            return (size / (1024 * 1024)) + "M";
        } else {
            return (size / (1024 * 1024 * 1024)) + "G";
        }
    }

    public static List coverList(Collection collection) {
        List values = new ArrayList();
        values.addAll(collection);
        return values;
    }


}
