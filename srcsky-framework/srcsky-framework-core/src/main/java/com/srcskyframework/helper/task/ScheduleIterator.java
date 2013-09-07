package com.srcskyframework.helper.task;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:20
 * Email: z82422@gmail.com
 * 计划任务，定时执行
 */

public interface ScheduleIterator {

    /**
     * Returns the next time that the related {@link SchedulerTask} should be run.
     *
     * @return the next time of execution
     */
    public Date next();
}
