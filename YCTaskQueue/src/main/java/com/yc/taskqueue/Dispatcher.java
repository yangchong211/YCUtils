package com.yc.taskqueue;


import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import java.util.concurrent.Executor;

/**
 * 异步任务分发器
 * <p>
 * 基于 AsyncTask 的封装，默认统一使用 AsyncTask 提供的线程池。
 *
 * @author yanglei (yangleiyanglei@didichuxing.com)
 * @date 2017/12/7 上午11:53
 */
public class Dispatcher {

    /**
     * Dispatch Runnable
     */
    public interface DispatchRunnable {

        /**
         * 工作线程调用方法
         */
        @WorkerThread
        void onWorkThread();

        /**
         * 主线程调用方法
         */
        @MainThread
        void onMainThread();
    }


    /**
     * 单线程线程组，线程池内只有一个线程
     *
     * @return single Thread Group
     */
    public static Executor singleGroup() {
        return AsyncTask.SERIAL_EXECUTOR;
    }

    /**
     * 线程池组，内部维护线程池，并提供线程复用能力
     *
     * @return Thread pool
     */
    public static Executor poolGroup() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    /**
     * 执行异步任务
     *
     * @param task {@link java.lang.Runnable}
     */
    public static void async(Runnable task) {
        async(poolGroup(), task);
    }

    /**
     * 执行异步任务
     *
     * @param threadGroup {@link Dispatcher#singleGroup()} or {@link Dispatcher#poolGroup()} or custom {@link Executor}
     * @param task        {@link java.lang.Runnable}
     */
    public static void async(Executor threadGroup, Runnable task) {
        new InnerAsyncTask0().executeOnExecutor(threadGroup, task);
    }

    /**
     * 执行异步任务
     *
     * @param task {@link DispatchRunnable}
     */
    public static void async(DispatchRunnable task) {
        async(poolGroup(), task);
    }

    /**
     * 执行异步任务
     *
     * @param threadGroup {@link Dispatcher#singleGroup()} or {@link Dispatcher#poolGroup()} or custom {@link Executor}
     * @param task        {@link DispatchRunnable}
     */
    public static void async(Executor threadGroup, DispatchRunnable task) {
        new InnerAsyncTask1().executeOnExecutor(threadGroup, task);
    }

    private static class InnerAsyncTask0 extends AsyncTask<Runnable, Void, Void> {

        @Override
        protected Void doInBackground(Runnable... tasks) {
            tasks[0].run();
            return null;
        }
    }

    private static class InnerAsyncTask1 extends AsyncTask<DispatchRunnable, Void, DispatchRunnable> {
        @Override
        protected DispatchRunnable doInBackground(DispatchRunnable... tasks) {
            tasks[0].onWorkThread();
            return tasks[0];
        }

        @Override
        protected void onPostExecute(DispatchRunnable task) {
            task.onMainThread();
        }
    }
}
