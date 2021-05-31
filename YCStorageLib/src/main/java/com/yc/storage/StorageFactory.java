package com.yc.storage;

import android.content.Context;
import android.support.annotation.NonNull;

final class StorageFactory {

    public static final String TAG = "Storage";

    private StorageFactory() {

    }


    @NonNull
    static <T> IStorage<T> createStorageImpl(String name, Context context, Class<T> classOfT) {
        StorageUtil.getILog().d(TAG, "createStorageImpl with name: " + name + "\n\tcontext: " + context + "\n\tclassOfT: " + classOfT);
        return new OptimizedSharedPreferencesStorage<>(name, context, classOfT);
    }
}

