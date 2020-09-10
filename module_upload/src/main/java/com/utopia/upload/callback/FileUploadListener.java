package com.utopia.upload.callback;

/**
 * 上传监听回调
 */
public interface FileUploadListener {
    default void onStart(){}

    default void onProgress(long currentSize, long totalSize){}

    default void onFinish(String response){}

    default void onError(String error){}
}
