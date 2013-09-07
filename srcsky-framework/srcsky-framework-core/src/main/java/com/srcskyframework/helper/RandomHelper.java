/**
 * 宝龙电商
 * com.srcskyframework.core.support
 * RandomHelper.java
 *
 * 2013-8-5-下午1:59
 * 2013宝龙公司-版权所有
 *
 */
package com.srcskyframework.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * RandomHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-5-下午1:59
 * @email zhanggj-hws@powerlong.com
 * @description 职责描述
 */
public class RandomHelper {

    /**
     * <b>功能：</b>返回一个List中随机项组成的List
     *
     * @param list   原始List
     * @param number 新List的大小
     * @return randomlist            随机生成的List
     */
    public static List getRandomList(List list, int number) {
        if (number < list.size()) {
            if (number < list.size() / 2) {
                List randomList = new ArrayList();
                int index;
                for (int i = 0; i < number; i++) {
                    index = (int) (Math.random() * (list.size() - 1));
                    randomList.add(list.get(index));
                    list.remove(index);
                }
                return randomList;
            } else {
                while (list.size() > number) {
                    int index = (int) (Math.random() * (list.size() - 1));
                    list.remove(index);
                }
                return list;
            }
        }
        return list;
    }


    /**
     * 32位UUID
     *
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }


    /**
     * 获得随即生成的字符串
     *
     * @param value
     * @return
     */
    public static String randomByScope(int value) {
        if (value == 0) return "1";
        value = (int) (Math.random() * value);
        return String.valueOf((value == 0) ? 1 : value);
    }

    public static String randomByLength(int length) {
        String result = new String();
        for (int i = 0; i < length; i++) {
            result += (int) (Math.random() * 10);
        }
        return result;
    }

    public static Integer getRandom(int value, int... length) {
        if (value == 0) return 1;
        value = (int) (Math.random() * value);
        return (value == 0) ? 1 : value;
    }
}
