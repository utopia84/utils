package com.zjmy.epub;

import android.text.TextUtils;


import com.zjmy.epub.bean.EpubBookInfo;
import com.zjmy.epub.zip.ReadEpubHeadInfo;

@SuppressWarnings("unused")
public class EpubInfoResolver {

    private volatile static EpubInfoResolver resolver;
    private String filePath;

    public static EpubInfoResolver getInstance() {
        if (resolver == null) {
            synchronized (EpubInfoResolver.class) {
                if (resolver == null) {
                    resolver = new EpubInfoResolver();
                }
            }
        }
        return resolver;
    }

    private EpubInfoResolver() {

    }

    public EpubInfoResolver initWithEpubFile(String path) {
        filePath = path;
        return this;
    }

    public EpubBookInfo startResolver() {
        EpubBookInfo epubBookInfo;
        if (TextUtils.isEmpty(filePath) || !filePath.toLowerCase().endsWith(".epub")){
            return null;
        }

        try {
            ReadEpubHeadInfo readEpubHeadInfo = new ReadEpubHeadInfo();
            epubBookInfo = readEpubHeadInfo.getePubBookInfo(filePath);
        } catch (Exception e) {
            throw e;
        }

        return epubBookInfo;
    }
}