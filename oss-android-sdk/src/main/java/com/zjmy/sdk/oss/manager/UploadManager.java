package com.zjmy.sdk.oss.manager;

import android.text.TextUtils;
import android.util.Log;

import com.zjmy.sdk.oss.OSS;
import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.callback.OSSProgressCallback;
import com.zjmy.sdk.oss.callback.SimpleOSSCallBack;
import com.zjmy.sdk.oss.common.OSSLog;
import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;
import com.zjmy.sdk.oss.model.PutObjectRequest;
import com.zjmy.sdk.oss.model.PutObjectResult;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 16:27
 */
public class UploadManager implements Upload {
    private final String mBucketName;
    private final OSS mOss;
    private SimpleOSSCallBack callback;
    private String mCallbackAddress;

    public UploadManager(String bucketName, OSS oss) {
        this.mBucketName = bucketName;
        this.mOss = oss;
    }

    public void setCallbackAddress(String callbackAddress) {
        this.mCallbackAddress = callbackAddress;
    }

    @Override
    public void upload(String objectName, String localFilePath) {
        OSSLog.logDebug("upload start");

        if (TextUtils.isEmpty(objectName) || TextUtils.isEmpty(localFilePath)) {
            Log.e("AsyncPutImage", "ObjectNull");
            return;
        }

        File file = new File(localFilePath);
        if (!file.exists()) {
            Log.e("LocalFile", localFilePath + "->FileNotExist");
            return;
        }

        // 构造上传请求
        OSSLog.logDebug("create PutObjectRequest ");
        PutObjectRequest put = new PutObjectRequest(mBucketName, objectName, localFilePath);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (mCallbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(new HashMap<String, String>() {{
                put("callbackUrl", mCallbackAddress);
                put("callbackBodyType", "application/json");
                put("callbackBody", "{\"fileName\":${x:var1},\"objectName\":${x:var2}}")
            }});

            put.setCallbackVars(new HashMap<String, String>() {{
                put("x:var1", file.getName());
                put("x:var2", objectName);
            }});
        }

        // 异步上传时可以设置进度回调
        if (callback != null) {
            put.setProgressCallback((request, currentSize, totalSize) -> {
                OSSLog.logDebug("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                callback.onProgress(request, currentSize, totalSize);
            });
        }

        OSSLog.logDebug(" asyncPutObject ");
        mOss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                OSSLog.logDebug("PutObject", "UploadSuccess");
                if (callback != null) {
                    callback.onSuccess(request, result);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                OSSLog.logDebug("PutObject", "UploadFailure");
                if (callback != null) {
                    callback.onFailure(request, clientExcepion, serviceException);
                }
            }
        });
    }

    @Override
    public void upload(String objectName, String localFilePath, SimpleOSSCallBack callback) {
        this.callback = callback;
        upload(objectName, localFilePath);
    }

}
