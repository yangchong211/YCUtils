package com.yc.storage;

import android.support.annotation.CheckResult;

public interface IStorage<T> {

    T getData();

    T getData(String key);

    void setData(T t);

    void setData(String key, T t);

    boolean getBoolean(final String key);

    void setBoolean(final String key, final boolean value);

    int getInt(final String key);

    void setInt(final String key, final int value);

    float getFloat(final String key);

    void setFloat(final String key, final float value);

    long getLong(final String key);

    void setLong(final String key, final long value);

    String getString(final String key);

    void setString(final String key, final String value);

    boolean has(final String key);

    void remove(final String key);

    void clear();

    IEditor edit();

    /*@Deprecated
    Serializable getSerializable(String key);

    @Deprecated
    void putSerializable(String key, Serializable serializable);*/

    interface IEditor {
        @CheckResult(suggest = "commit")
        IEditor putBoolean(final String key, final boolean value);

        @CheckResult(suggest = "commit")
        IEditor putInt(final String key, final int value);

        @CheckResult(suggest = "commit")
        IEditor putFloat(final String key, final float value);

        @CheckResult(suggest = "commit")
        IEditor putLong(final String key, final long value);

        @CheckResult(suggest = "commit")
        IEditor putString(final String key, final String value);

        void commit();

        void commit(final Callback callback);
    }

    interface Callback {
        void done(final boolean result);
    }
}

