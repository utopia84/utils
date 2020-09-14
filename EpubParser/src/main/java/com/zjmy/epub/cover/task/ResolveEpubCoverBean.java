package com.zjmy.epub.cover.task;

import com.zjmy.epub.callback.OnCompressListener;

public class ResolveEpubCoverBean {
    private String epubFilePath;//epub文件地址
    private OnCompressListener callback;//回调

    public ResolveEpubCoverBean(String epubFilePath, OnCompressListener callback) {
        this.epubFilePath = epubFilePath;
        this.callback = callback;
    }


    public String getEpubFilePath() {
        return epubFilePath;
    }

    public OnCompressListener getCallback() {
        return callback;
    }

    public void setCallback(OnCompressListener callback) {
        this.callback = callback;
    }
}
