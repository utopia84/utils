package com.zjmy.epub.utils;

import android.graphics.Bitmap;
import android.os.Build;

public class BitmapUtil {

    private static final int ALPHA_8_BYTES_PER_PIXEL = 1;

    private static final int ARGB_4444_BYTES_PER_PIXEL = 2;

    private static final int ARGB_8888_BYTES_PER_PIXEL = 4;

    private static final int RGB_565_BYTES_PER_PIXEL = 2;

    public static long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null)
            return 0;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return bitmap.getByteCount();
    }

    public static long getSizeInBytes(int width, int height, Bitmap.Config config) {
        if (width <= 0 || height <= 0)
            return 0L;
        if (config == null)
            config = Bitmap.Config.ARGB_8888;
        switch (config) {
            case ARGB_8888:
                return ARGB_8888_BYTES_PER_PIXEL * width * height;
            case ALPHA_8:
                return ALPHA_8_BYTES_PER_PIXEL * width * height;
            case ARGB_4444:
                return ARGB_4444_BYTES_PER_PIXEL * width * height;
            case RGB_565:
                return RGB_565_BYTES_PER_PIXEL * width * height;
            default:
                return 0L;
        }
    }
}
