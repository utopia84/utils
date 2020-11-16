package com.zjmy.sdk.oss;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.zjmy.sdk.oss.callback.OSSProgressCallback;
import com.zjmy.sdk.oss.callback.SimpleOSSCallBack;
import com.zjmy.sdk.oss.common.utils.FileUtils;
import com.zjmy.sdk.oss.exception.ClientConfiguration;
import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.common.OSSLog;
import com.zjmy.sdk.oss.common.auth.OSSAuthCredentialsProvider;
import com.zjmy.sdk.oss.common.auth.OSSCredentialProvider;
import com.zjmy.sdk.oss.common.utils.IOUtils;
import com.zjmy.sdk.oss.manager.Download;
import com.zjmy.sdk.oss.manager.DownloadManager;
import com.zjmy.sdk.oss.manager.Upload;
import com.zjmy.sdk.oss.manager.UploadManager;
import com.zjmy.sdk.oss.model.GetObjectRequest;
import com.zjmy.sdk.oss.model.GetObjectResult;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;
import com.zjmy.sdk.oss.model.PutObjectRequest;
import com.zjmy.sdk.oss.model.PutObjectResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 11:06
 */
public class OssService implements Download , Upload {
    private volatile static OssService ossService;
    private final DownloadManager downloadManager;
    private final UploadManager uploadManager;


    private OssService(OSS oss, String bucket) {
        downloadManager = new DownloadManager(bucket,oss);
        uploadManager = new UploadManager(bucket,oss);
    }


    public static OssService get(){
        if (ossService == null){
            throw new IllegalStateException("使用OssService前，需要在Application里面调用OssService.initOSS()");
        }
        return ossService;
    }

    public static void initOSS(Application application,OssConfig config) {
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(3); // 失败后最大重试次数，默认3次

        OSS oss = new OSSClient(application, config.getEndpoint(), new OSSAuthCredentialsProvider(config.getStsServerUrl()), conf);
        OSSLog.enableLog();
        ossService =  new OssService(oss, config.getBucket());

        //设置服务端回调
        ossService.setCallbackAddress(config.getCallbackUrl());
    }

    public void setCallbackAddress(String callbackAddress) {
        uploadManager.setCallbackAddress(callbackAddress);
    }

    @Override
    public void download(String objectName, String localFilePath) {
        downloadManager.download(objectName,localFilePath);
    }

    @Override
    public void download(String objectName, String localFilePath, SimpleOSSCallBack callback) {
        downloadManager.download(objectName,localFilePath,callback);
    }

    @Override
    public void upload(String objectName, String localFilePath) {
        uploadManager.upload(objectName,localFilePath);
    }

    @Override
    public void upload(String objectName, String localFilePath, SimpleOSSCallBack callback) {
        uploadManager.upload(objectName,localFilePath,callback);
    }
}
