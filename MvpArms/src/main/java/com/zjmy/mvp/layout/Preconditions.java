package com.zjmy.mvp.layout;

import android.os.Looper;

/**
 * 校验检查类
 */
public final class Preconditions {

    private Preconditions() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static <T> void checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
    }

    public static void checkMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("Not in applications main thread");
        }
    }
}
