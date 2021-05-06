package com.yc.taskqueue;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.util.Log;


import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * 异步串行任务队列
 *
 * @author yanglei (yangleiyanglei@didichuxing.com)
 * @date 2017/12/14 下午5:01
 */
public class SerialTaskQueue {
    /**
     * 任务追加模式
     */
    public enum AppendMode {
        /**
         * 正常追加
         */
        Normal,

        /**
         * 如果等待队列中包含同样的任务(不包括正在执行的任务)，替换原有任务
         */
        Replace,

        /**
         * 如果等待队列中包含同样的任务(不包括正在执行的任务)，放弃追加任务
         */
        Discard,

        /**
         * 如果等待队列中包含同样的任务(包括已经正在执行的任务)，替换原有任务
         */
        ReplaceStrict,

        /**
         * 如果等待队列中包含同样的任务(包括已经正在执行的任务)，放弃追加任务
         */
        DiscardStrict
    }

    static abstract class Work implements Dispatcher.DispatchRunnable {
        volatile boolean cancel;
        volatile boolean finished;

        abstract void onCancel();

        abstract String getCategory();
    }

    private final ArrayDeque<Work> mWorks = new ArrayDeque<>();
    private Work mActive;

    @MainThread
    public @NonNull
    Cancelable append(final Task task, AppendMode mode) {
        if (mode != AppendMode.Normal) {
            if (mode == AppendMode.ReplaceStrict) {
                if (mActive != null && mActive.getCategory().equals(task.getCategory())) {
                    mActive.cancel = true;
                    if (!mActive.finished) {
                        mActive.onCancel();
                    }
                }
            } else if (mode == AppendMode.DiscardStrict) {
                if (mActive != null && mActive.getCategory().equals(task.getCategory())) {
                    logInfo("Cancel " + task + " category: " + task.getCategory() + " mode: " + mode);
                    task.onCancel();
                    return new EmptyCancelable();
                }
            }

            final ArrayDeque<Work> works;
            synchronized (mWorks) {
                works = new ArrayDeque<>(mWorks);
            }
            for (Iterator<Work> iter = works.iterator(); iter.hasNext(); ) {
                Work work = iter.next();
                if (work.getCategory().equals(task.getCategory())) {
                    if (mode == AppendMode.Replace || mode == AppendMode.ReplaceStrict) {
                        logInfo("Cancel " + work + " mode: " + mode);
                        work.cancel = true;
                        if (!work.finished) {
                            work.onCancel();
                        }
                        logInfo(work.toString() + " is Replaced.");
                        continue;
                    } else if (mode == AppendMode.Discard || mode == AppendMode.DiscardStrict) {
                        logInfo("Cancel " + task + " category: " + task.getCategory() + " mode: " + mode);
                        task.onCancel();
                        return new EmptyCancelable();
                    }
                }
            }
        }
        final Work work = execute(task);
        logInfo("Execute " + work + " mode: " + mode + "\n\tmActive: " + mActive);
        return new Cancelable() {
            @Override
            public void cancel() {
                if (!work.cancel && !work.finished) {
                    work.onCancel();
                }
            }
        };
    }

    public void clear() {
        for (Work work : mWorks) {
            if (!work.finished) {
                work.onCancel();
            }
        }
        synchronized (mWorks) {
            mWorks.clear();
        }
    }

    private void logInfo(String info) {
        try {
            Log.i("log",info);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private synchronized Work execute(final Task task) {
        Work work = new Work() {
            @Override
            public void onWorkThread() {
                logInfo("onWorkThread " + this);
                if (finished) return;
                if (cancel) {
                    onCancel();
                } else {
                    task.onWorkThread();
                }
            }

            @Override
            public void onMainThread() {
                logInfo("onMainThread " + this + "\n\tmActive: " + mActive);
                if (finished) {
                    if (mActive == this) {
                        scheduleNext();
                    }
                    return;
                }
                if (cancel) {
                    onCancel();
                } else {
                    task.onMainThread();
                    finished = true;
                    scheduleNext();
                }
            }

            @Override
            public void onCancel() {
                logInfo("onCancel " + this + "\n\tmActive: " + mActive);
                task.onCancel();
                finished = true;
                if (mActive == this) {
                    scheduleNext();
                }
            }

            @Override
            String getCategory() {
                return task.getCategory();
            }

            @Override
            public String toString() {
                return task.toString() + " category: " + getCategory() + " cancel: " + cancel + " finished: " + finished;
            }
        };

        synchronized (mWorks) {
            mWorks.offer(work);
        }

        if (mActive == null) {
            scheduleNext();
        }
        return work;
    }

    private void scheduleNext() {
        synchronized (mWorks) {
            mActive = mWorks.poll();
        }
        if (mActive != null) {
            if (mActive.cancel) {
                if (!mActive.finished)
                    mActive.onCancel();
                scheduleNext();
            } else {
                Dispatcher.async(mActive);
            }
        }
    }

    static class EmptyCancelable implements Cancelable {

        @Override
        public void cancel() {
            //ignore
        }
    }
}

