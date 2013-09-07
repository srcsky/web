package com.srcskyframework.helper.task;


import com.srcskyframework.helper.ValidHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:20
 * Email: z82422@gmail.com
 * Scheduler 与  ThreadPool 的封装 提供 了一个 服务接口
 */
public class ExecuteTask {
    private static Scheduler scheduler = new Scheduler();

    /**
     * 定时执行
     *
     * @param schedulerTask
     * @param iterator
     * @param execute
     */
    public static void executeTask(SchedulerTask schedulerTask, ScheduleIterator iterator, Boolean... execute) {
        if (!ValidHelper.isEmpty(execute) && execute.length > 0 && execute[0]) {
            schedulerTask.run();
        }
        scheduler.schedule(schedulerTask, iterator);
    }

    /**
     * 线程迟,只要有任务存在就执行
     *
     * @param schedulerTask
     */
    public static void executeTask(Runnable schedulerTask) {
        ThreadPool.executeTask(schedulerTask);
    }
}
