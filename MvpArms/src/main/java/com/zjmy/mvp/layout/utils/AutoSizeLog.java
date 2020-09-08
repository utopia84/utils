package com.zjmy.mvp.layout.utils;

import android.util.Log;

public class AutoSizeLog {
    private static final String TAG = "AndroidAutoSize";
    private static boolean debug;

    private AutoSizeLog() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        AutoSizeLog.debug = debug;
    }

    public static void e(String message) {
        if (debug) {
            Log.e(TAG, message);
        }
    }
}
