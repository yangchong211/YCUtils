package com.yc.storage;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

final class StorageUtil {

    private static boolean sJsonParserOn = false;
    private static Gson mGson;

    public static final String TAG = "StorageUtil";
    private static ILog sLog = new ILog() {
        @Override
        public void d(String tag, String msg) {
            // Do nothing;
        }
    };

    static {
        try {
            Class.forName("com.didi.soda.nova.json.parser.JsonUtils");
            sJsonParserOn = true;
        } catch (ClassNotFoundException e) {
            sJsonParserOn = false;
            e.printStackTrace();
        }
        Log.d(TAG, "isJsonParserOn: " + sJsonParserOn);
        if (!sJsonParserOn) {
            mGson = new Gson();
        }
    }

    private StorageUtil() {
    }

    static <T> T fromJson(String jsonStr, Class<T> classOfT) {
        if (sJsonParserOn) {
            return JsonUtils.fromJson(jsonStr, classOfT);
        } else {
            return mGson.fromJson(jsonStr, classOfT);
        }
    }

    static String toJson(Object obj) {
        if (sJsonParserOn) {
            return JsonUtils.toJson(obj);
        } else {
            return mGson.toJson(obj);
        }
    }

    static String encodeBase64(@NonNull String data) {
        return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
    }

    static String decodeBase64(@NonNull String data) {
        return new String(Base64.decode(data, Base64.DEFAULT));
    }

    static void setILog(@NonNull ILog log) {
        sLog = log;
    }

    @NonNull
    static ILog getILog() {
        return sLog;
    }

}

