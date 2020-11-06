package com.zjmy.viewbox.util;

import android.os.Looper;

public class StateBoxUtil {

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @SafeVarargs
    public static <T> boolean checkNotNull(T... arg) {
        return checkNotNull( "Argument must not be null" , arg);
    }


    @SafeVarargs
    public static <T> boolean checkNotNull(String message, T... arg) {
        if (arg == null || arg.length == 0){
            return false;
        }

        for (T t : arg){
            if (t == null){
                return false;
            }
        }

        return true;
    }
}
