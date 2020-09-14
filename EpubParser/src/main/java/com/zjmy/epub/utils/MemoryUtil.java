package com.zjmy.epub.utils;

import android.graphics.Bitmap;

public class MemoryUtil {

    public static boolean memoryEnough(int width, int height, Bitmap.Config config, float safeMemory) {
        Runtime runtime = Runtime.getRuntime();
        long free = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
        long allocation = BitmapUtil.getSizeInBytes(width, height, config);
        Logger.i("free : " + free / 1024 / 1024 + "MB, need : " + allocation / 1024 / 1024 + "MB");
        return allocation * safeMemory < free;
    }

    public static boolean memoryEnough(Bitmap bitmap, float safeMemory) {
        Runtime runtime = Runtime.getRuntime();
        long free = runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
        long allocation = BitmapUtil.getSizeInBytes(bitmap);
        Logger.i("free : " + free / 1024 / 1024 + "MB, need : " + allocation / 1024 / 1024 + "MB");
        return allocation * safeMemory < free;
    }
}
