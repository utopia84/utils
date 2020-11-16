package com.zjmy.sdk.oss.manager;

import com.zjmy.sdk.oss.OSS;
import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.callback.OSSProgressCallback;
import com.zjmy.sdk.oss.callback.SimpleOSSCallBack;
import com.zjmy.sdk.oss.common.OSSLog;
import com.zjmy.sdk.oss.common.utils.FileUtils;
import com.zjmy.sdk.oss.common.utils.IOUtils;
import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.model.GetObjectRequest;
import com.zjmy.sdk.oss.model.GetObjectResult;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 15:51
 */
public class DownloadManager implements Download{
    private final String mBucketName;
    private final OSS mOss;
    private SimpleOSSCallBack mCallback;

    public DownloadManager(String bucketName, OSS oss) {
        this.mBucketName = bucketName;
        this.mOss = oss;
    }

    @Override
    public void download(String objectName, String localFilePath, SimpleOSSCallBack callback) {
        mCallback = callback;
        download(objectName,localFilePath);
    }

    @Override
    public void download(String objectName, String localFilePath) {

        GetObjectRequest get = new GetObjectRequest(mBucketName, objectName);

        //设置下载进度回调
        if (mCallback != null) {
            get.setProgressListener((request, currentSize, totalSize) -> {
                OSSLog.logDebug("getobj_progress: " + currentSize + "  total_size: " + totalSize, false);
                mCallback.onProgress(request, currentSize, totalSize);
            });
        }

        mOss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                InputStream inputStream = result.getObjectContent();
                try {
                    FileUtils.writeFileFromIS(localFilePath,inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mCallback != null){
                        mCallback.onFailure(request,new ClientException(e),null);
                    }
                }finally {
                    IOUtils.safeClose(inputStream);
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (mCallback != null){
                    mCallback.onFailure(request,clientExcepion,serviceException);
                }
            }
        });

    }


}
