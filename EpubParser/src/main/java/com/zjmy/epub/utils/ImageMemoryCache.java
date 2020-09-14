package com.zjmy.epub.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

public class ImageMemoryCache {
    private LruCache<String, Bitmap> mMemoryCache;
    private static volatile ImageMemoryCache cache;

    public static ImageMemoryCache getInstance() {
        if (cache == null) {
            synchronized (ImageMemoryCache.class) {
                if (cache == null) {
                    cache = new ImageMemoryCache();
                }
            }
        }
        return cache;
    }

    private ImageMemoryCache() {
        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        //int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        //int cacheSize = maxMemory / 8;
        int cacheSize = 5 * 1024 * 1024;//5M
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() ;
            }
        };
    }


    public void putBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            //Log.e("test","我从内存缓存中获取到了bitmap:"+key);
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void clear() {
        mMemoryCache.evictAll();
    }
}
