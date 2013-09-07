package com.srcskyframework.helper.task;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Zhanggaojiang
 * Date: 11-1-19
 * Time: 上午10:20
 * Email: z82422@gmail.com
 * 线程池
 */
public class ThreadPool {

    private static Log loginfo = LogFactory.getLog(ThreadPool.class);
    private static ThreadPool pool = new ThreadPool();/*线程池单一实例*/
    private static LinkedList queue;/*线程池的任务队列*/
    private static ThreadWorks[] threads;/*模拟线程池*/

    private ThreadPool() {
        queue = new LinkedList();
        threads = new ThreadWorks[50];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new ThreadWorks();
            threads[i].start();
        }
    }

    public static void executeTask(Runnable runnable) {
        synchronized (queue) {
            queue.add(runnable);
            queue.notify();
        }
    }

    class ThreadWorks extends Thread {
        public void run() {
            Runnable run;
            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException ex) {
                            loginfo.debug("线程池_等待时_Error:" + ex.getMessage(), ex);
                        }
                    }
                    run = (Runnable) queue.removeFirst();
                }
                try {
                    run.run();
                } catch (Exception ex) {
                    loginfo.debug("线程池_执行时_Error:" + ex.getMessage(), ex);
                }
            }
        }
    }
}
