package com.audio.player.util;

import android.util.Log;

/**
 * @作者 邸昌顺
 * @时间 2019/3/12 15:08
 * @描述
 */
public class UICLog {

    private static boolean debug = true;
    private static String TAG = "test_d";

    public static void enableDebug(boolean debug){
        UICLog.debug = debug;
    }

    public static void setTAG(String tag){
        UICLog.TAG = tag;
    }

    public static void e(String msg){
        e(TAG, msg);
    }

    public static void d(String msg){
        d(TAG, msg);
    }

    public static void e(String TAG, String msg){
        if(debug) Log.e(TAG, msg);
    }

    public static void d(String TAG, String msg){
        if(debug) Log.e(TAG, msg);
    }
}
