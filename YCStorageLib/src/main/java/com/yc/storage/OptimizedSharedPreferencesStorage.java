package com.yc.storage;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 新的Storage实现：基于原生SharedPreferences，对Object做JSON String处理后存储。不支持序列化接口。
 *
 * @author sxl (sunxiaoling@didiglobal.com)
 * @since 2020/4/23.
 */
class OptimizedSharedPreferencesStorage<T> implements IStorage<T> {
    public static final String TAG = "OptSharedPrefStorage";

    private static final String KEY_DATA = "StorageOptimized.Key";
    private final SharedPreferences mSharedPref;
    private final SharedPreferences.Editor mSharedPrefEditor;
    private Class<T> classOfT;

    // Data Cache: Key-JSONStrOfT
    private final ConcurrentMap<String, String> mDataJSONStrCache = new ConcurrentHashMap<>();

    @SuppressLint("CommitPrefEdits")
    OptimizedSharedPreferencesStorage(final String name, Context context, Class<T> classOfT) {
        this.mSharedPref = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        this.mSharedPrefEditor = mSharedPref.edit();
        this.classOfT = classOfT;
        StorageUtil.getILog().d(TAG, "Constructor classOfT: " + classOfT);
    }

    @Override
    public T getData() {
        return getData(KEY_DATA);
    }

    @Override
    public T getData(String key) {
        String logMsg = "getData classOfT=" + classOfT + ", key=" + key + ", mDataJSONStrCache=" + mDataJSONStrCache;

        String jsonStrOfData = "";
        // 优先读取内存缓存
        if (mDataJSONStrCache.containsKey(key)) {
            logMsg += "\n\t--> Cache hit!";
            jsonStrOfData = mDataJSONStrCache.get(key);
        } else { // 没有缓存，读存储
            final String raw = getString(key);
            final String decodedStr = StorageUtil.decodeBase64(raw);
            logMsg += "\n\t--> No cache and read data from storage!";
            // Cache decoded JSONStr
            if (!TextUtils.isEmpty(decodedStr)) {
                mDataJSONStrCache.put(key, decodedStr);
                jsonStrOfData = decodedStr;
            }
        }
        T data = StorageUtil.fromJson(jsonStrOfData, classOfT);
        logMsg += "\n\t--> jsonStrOfData=" + jsonStrOfData + "\n\t--> data=" + data;
        StorageUtil.getILog().d(TAG, logMsg);

        return data;
    }

    @Override
    public void setData(T data) {
        setData(KEY_DATA, data);
    }

    @Override
    public void setData(String key, T data) {
        // Check if data content changed
        final String cachedJsonStr = (key == null) ? "" : mDataJSONStrCache.get(key);
        final String jsonStrOfData = StorageUtil.toJson(data);
        String logMsg = "setData classOfT=" + classOfT + ", key=" + key;
        logMsg += " data=" + data + "\n\t--> cachedJsonStr=" + cachedJsonStr + "\n\t--> jsonStrOfData=" + jsonStrOfData;
        if(jsonStrOfData.equals(cachedJsonStr)) {
            logMsg += "\n\tNo diff and return!";
            StorageUtil.getILog().d(TAG, logMsg);
            return;
        }

        logMsg += "\n\t--> Data changed and write new data into storage!";
        StorageUtil.getILog().d(TAG, logMsg);
        if (data == null) {
            mDataJSONStrCache.remove(key);
        } else {
            if (key != null) {
                mDataJSONStrCache.put(key, jsonStrOfData);
            }
        }

        final String encodedStr = StorageUtil.encodeBase64(jsonStrOfData);
        setString(key, encodedStr);
    }

    @Override
    public boolean getBoolean(String key) {
        return mSharedPref.getBoolean(key, false);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        commit(mSharedPrefEditor.putBoolean(key, value));
    }

    @Override
    public int getInt(String key) {
        return mSharedPref.getInt(key, 0);
    }

    @Override
    public void setInt(String key, int value) {
        commit(mSharedPrefEditor.putInt(key, value));
    }

    @Override
    public float getFloat(String key) {
        return mSharedPref.getFloat(key, 0f);
    }

    @Override
    public void setFloat(String key, float value) {
        commit(mSharedPrefEditor.putFloat(key, value));
    }

    @Override
    public long getLong(String key) {
        return mSharedPref.getLong(key, 0L);
    }

    @Override
    public void setLong(String key, long value) {
        commit(mSharedPrefEditor.putLong(key, value));
    }

    @Override
    public String getString(String key) {
        return mSharedPref.getString(key, "");
    }

    @Override
    public void setString(String key, String value) {
        commit(mSharedPrefEditor.putString(key, value));
    }

    @Override
    public boolean has(String key) {
        return mSharedPref.contains(key);
    }

    @Override
    public void remove(String key) {
        commit(mSharedPrefEditor.remove(key));
    }

    @Override
    public void clear() {
        mDataJSONStrCache.clear();
        commit(mSharedPrefEditor.clear());
    }

    @Override
    public IEditor edit() {
        return new StorageEditor(mSharedPrefEditor);
    }

    /*@Override
    public void putSerializable(String key, Serializable serializable) {
        throw new UnsupportedOperationException("putSerializable");
    }

    @Override
    public Serializable getSerializable(String key) {
        throw new UnsupportedOperationException("getSerializable");
    }*/

    private boolean commit(@NonNull SharedPreferences.Editor editor) {
        editor.apply();
        return true;
    }

    private class StorageEditor implements IEditor {
        private SharedPreferences.Editor mEditor;

        StorageEditor(@NonNull SharedPreferences.Editor editor) {
            this.mEditor = editor;
        }

        @Override
        public IEditor putBoolean(String key, boolean value) {
            mEditor.putBoolean(key, value);
            return this;
        }

        @Override
        public IEditor putInt(String key, int value) {
            mEditor.putInt(key, value);
            return this;
        }

        @Override
        public IEditor putFloat(String key, float value) {
            mEditor.putFloat(key, value);
            return this;
        }

        @Override
        public IEditor putLong(String key, long value) {
            mEditor.putLong(key, value);
            return this;
        }

        @Override
        public IEditor putString(String key, String value) {
            mEditor.putString(key, value);
            return this;
        }

        @Override
        public void commit() {
            commit(null);
        }

        @Override
        public void commit(Callback callback) {
            boolean result = OptimizedSharedPreferencesStorage.this.commit(mEditor);
            if (callback != null) {
                callback.done(result);
            }
        }
    }
}

