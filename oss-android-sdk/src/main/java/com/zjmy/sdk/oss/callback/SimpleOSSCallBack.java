package com.zjmy.sdk.oss.callback;

import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 16:47
 */
public abstract class SimpleOSSCallBack implements OSSCompletedCallback , OSSProgressCallback{
    @Override
    public void onSuccess(OSSRequest request, OSSResult result) {

    }

    @Override
    public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {

    }

    @Override
    public void onProgress(Object request, long currentSize, long totalSize) {

    }
}
