package com.srcskyframework.helper;

/**
 * TestHelper
 *
 * @author Zhanggj
 * @version 1.0.0
 * @date 2013-8-5-下午2:02
 * @email z82422@gmail.com
 * @description 职责描述
 */
public class TestHelper {

    /**
     * 测试方法, 执行 count次   Runnable.run
     *
     * @param run   执行逻辑
     * @param count 执行次数
     */
    public static void test(Runnable run, int count) {
        long apptime = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            long start = System.currentTimeMillis();
            run.run();
            System.out.println("第" + i + "次执行耗时:" + (System.currentTimeMillis() - start));
        }
        apptime = (System.currentTimeMillis() - apptime);
        System.out.println("执行" + count + "次总耗时:" + apptime + ",平均耗时:" + (apptime / count));
    }

}
