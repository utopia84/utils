package com.zjmy.sdk.oss.callback;


public interface OSSProgressCallback<T> {
    void onProgress(T request, long currentSize, long totalSize);
}
