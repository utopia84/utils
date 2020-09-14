package com.zjmy.epub.cover.task;

import android.graphics.Bitmap;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.zjmy.epub.bean.EpubBookInfo;
import com.zjmy.epub.callback.OnCompressListener;
import com.zjmy.epub.cover.CompressEngine;
import com.zjmy.epub.utils.ImageDiskCache;
import com.zjmy.epub.utils.ImageMemoryCache;
import com.zjmy.epub.zip.ReadEpubHeadInfo;

public class ResolverTask implements Runnable {
    private String epubFilePath;//epub文件地址
    private OnCompressListener callback;//回调
    private String coverName;

    public ResolverTask(String path, String coverName, OnCompressListener listener) {
        this.epubFilePath = path;
        this.callback = listener;
        this.coverName = coverName;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
        if (!TextUtils.isEmpty(epubFilePath) && !TextUtils.isEmpty(coverName) && callback != null) {
            Bitmap bitmap = null;

            if (epubFilePath.toLowerCase().endsWith(".epub")) {
                ReadEpubHeadInfo readEpubHeadInfo = new ReadEpubHeadInfo();
                EpubBookInfo book = readEpubHeadInfo.getePubBookInfo(epubFilePath);
                File epubFile = new File(epubFilePath);
                if (book != null && !TextUtils.isEmpty(book.getCoverPath()) && epubFile.exists()) {

                    ZipFile zipFile = null;
                    try {
                        zipFile = new ZipFile(epubFile);
                        ZipEntry zipEntry = zipFile.getEntry(book.getCoverPath());
                        bitmap = new CompressEngine(zipFile, zipEntry).compress();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (zipFile != null) {
                            try {
                                zipFile.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (bitmap != null) {
                //加缓存
                ImageMemoryCache.getInstance().putBitmapToMemoryCache(coverName, bitmap);
                ImageDiskCache.getInstance().putBitmap(coverName, bitmap);//缓存
                callback.onSuccess(bitmap);
            } else {
                callback.onError();
            }
        }
    }
}
