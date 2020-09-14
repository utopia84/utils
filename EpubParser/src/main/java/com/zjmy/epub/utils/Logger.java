package com.zjmy.epub.utils;

import android.util.Log;

public class Logger {

    private static final String TAG = "epub compressor logger";

    private static boolean mIsDebug = false;

    public static void debug(boolean isDebug) {
        mIsDebug = isDebug;
    }

    public static boolean isDebug() {
        return mIsDebug;
    }

    public static void e(String message) {
        if (isDebug())
            Log.e(TAG, message);
    }

    public static void i(String message) {
        if (isDebug())
            Log.i(TAG, message);
    }
}
