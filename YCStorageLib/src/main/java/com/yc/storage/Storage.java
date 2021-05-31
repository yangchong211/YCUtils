package com.yc.storage;

import android.content.Context;

public abstract class Storage<T> implements IStorage<T> {

    private IStorage<T> mStorage;

    public Storage(Class<T> classOfT, Context context) {
        this.mStorage = StorageFactory.createStorageImpl(getClass().getName(),context, classOfT);
    }

    public Storage(String name, Class<T> classOfT , Context context) {
        this.mStorage = StorageFactory.createStorageImpl(name,context, classOfT);
    }

    public Storage(Context context, Class<T> classOfT) {
        this.mStorage = StorageFactory.createStorageImpl(getClass().getName(), context, classOfT);
    }

    public Storage(String name, Context context, Class<T> classOfT) {
        this.mStorage = StorageFactory.createStorageImpl(name, context, classOfT);
    }

    @Override
    public T getData() {
        T data = null;
        try {
            data = this.mStorage.getData();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public T getData(String key) {
        T data = null;
        try {
            data = this.mStorage.getData(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void setData(T data) {
        try {
            this.mStorage.setData(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setData(String key, T data) {
        try {
            this.mStorage.setData(key, data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getBoolean(String key) {
        return this.mStorage.getBoolean(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        this.mStorage.setBoolean(key, value);
    }

    @Override
    public int getInt(String key) {
        return this.mStorage.getInt(key);
    }

    @Override
    public void setInt(String key, int value) {
        this.mStorage.setInt(key, value);
    }

    @Override
    public float getFloat(String key) {
        return this.mStorage.getFloat(key);
    }

    @Override
    public void setFloat(String key, float value) {
        this.mStorage.setFloat(key, value);
    }

    @Override
    public long getLong(String key) {
        return this.mStorage.getLong(key);
    }

    @Override
    public void setLong(String key, long value) {
        this.mStorage.setLong(key, value);
    }

    @Override
    public String getString(String key) {
        return this.mStorage.getString(key);
    }

    @Override
    public void setString(String key, String value) {
        this.mStorage.setString(key, value);
    }

    @Override
    public boolean has(String key) {
        return this.mStorage.has(key);
    }

    @Override
    public void remove(String key) {
        this.mStorage.remove(key);
    }

    @Override
    public void clear() {
        this.mStorage.clear();
    }

    @Override
    public IEditor edit() {
        return mStorage.edit();
    }

    public static void setILog(ILog log) {
        StorageUtil.setILog(log);
    }
}

