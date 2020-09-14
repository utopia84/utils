package com.zjmy.epub.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;


public class ImageDiskCache {
    private static volatile ImageDiskCache cache;
    private static final int MAX_SIZE = 50 * 1024 * 1024;//50MB
    private DiskLruCache diskLruCache;

    public static ImageDiskCache getInstance() {
        if (cache == null) {
            synchronized (ImageDiskCache.class) {
                if (cache == null) {
                    cache = new ImageDiskCache();
                }
            }
        }
        return cache;
    }

    private ImageDiskCache() {
        if (diskLruCache == null || diskLruCache.isClosed()) {
            try {
                String cachePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "Android"
                        + File.separator + "bitmap";
                File cacheDir = new File(cachePath);
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }

                diskLruCache = DiskLruCache.open(cacheDir,1,1,MAX_SIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void putBitmap(String key, Bitmap bitmap) {
        if (bitmap == null || diskLruCache == null || TextUtils.isEmpty(key)) {
            return;
        }

        OutputStream out = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot == null) {
                final DiskLruCache.Editor editor = diskLruCache.edit(key);
                if (editor != null) {
                    out = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    editor.commit();
                    out.close();
                }
            } else {
                snapshot.getInputStream(0).close();
            }
        } catch (Exception e) {
            Log.e(TAG, "addBitmapToCache - " + e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public Bitmap getBitmap(String key , int width , int height) {
        //Log.e("test","fileName:"+key);
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            final DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            if (snapshot != null) {
                inputStream = snapshot.getInputStream(0);
                if (inputStream != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;//cpu资源换内存
                    bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getBitmapFromDiskCache - " + e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ignored) {
            }
        }
        return bitmap;
    }
}
