package com.srcskyframework.helper.task;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:20
 * Email: z82422@gmail.com
 * 计划任务，定时执行
 */
public class Scheduler {
    class SchedulerTimerTask extends TimerTask {
        private SchedulerTask schedulerTask;
        private ScheduleIterator iterator;

        public SchedulerTimerTask(SchedulerTask schedulerTask,
                                  ScheduleIterator iterator) {
            this.schedulerTask = schedulerTask;
            this.iterator = iterator;
        }

        public void run() {
            schedulerTask.run();
            reschedule(schedulerTask, iterator);
        }
    }

    private final Timer timer = new Timer();

    public Scheduler() {
    }

    /**
     * Terminates this <code>Scheduler</code>, discarding any currently scheduled tasks. Does not interfere with a currently executing task (if it exists). Once a scheduler has been terminated, its execution thread terminates gracefully, and no more tasks may be scheduled on it.
     * <p/>
     * Note that calling this method from within the run method of a scheduler task that was invoked by this scheduler absolutely guarantees that the ongoing task execution is the last task execution that will ever be performed by this scheduler.
     * <p/>
     * This method may be called repeatedly; the second and subsequent calls have no effect.
     */

    public void cancel() {
        timer.cancel();
    }

    /**
     * Schedules the specified task for execution according to the specified schedule. If times specified by the <code>ScheduleIterator</code> are in the past they are scheduled for immediate execution.
     * <p/>
     *
     * @param schedulerTask task to be scheduled
     * @param iterator      iterator that describes the schedule
     * @throws IllegalStateException if task was already scheduled or cancelled, scheduler was cancelled, or scheduler thread terminated.
     */

    public void schedule(SchedulerTask schedulerTask, ScheduleIterator iterator) {
        Date time = iterator.next();
        if (time == null) {
            schedulerTask.cancel();
        } else {
            synchronized (schedulerTask.lock) {
                if (schedulerTask.state != SchedulerTask.VIRGIN) {
                    throw new IllegalStateException("Task already scheduled " +
                            "or cancelled");
                }
                schedulerTask.state = SchedulerTask.SCHEDULED;
                schedulerTask.timerTask = new SchedulerTimerTask(schedulerTask, iterator);
                timer.schedule(schedulerTask.timerTask, time);
            }
        }
    }

    private void reschedule(SchedulerTask schedulerTask,
                            ScheduleIterator iterator) {

        Date time = iterator.next();
        if (time == null) {
            schedulerTask.cancel();
        } else {
            synchronized (schedulerTask.lock) {
                if (schedulerTask.state != SchedulerTask.CANCELLED) {
                    schedulerTask.timerTask = new SchedulerTimerTask(schedulerTask, iterator);
                    timer.schedule(schedulerTask.timerTask, time);
                }
            }
        }
    }

}