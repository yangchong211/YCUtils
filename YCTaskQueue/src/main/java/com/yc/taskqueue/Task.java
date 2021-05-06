package com.yc.taskqueue;

public abstract class Task implements Dispatcher.DispatchRunnable {

    public abstract void onCancel();

    public String getCategory() {
        return getClass().getName();
    }

}
