package com.zjmy.sdk.oss.callback;

import com.zjmy.sdk.oss.exception.ClientException;
import com.zjmy.sdk.oss.exception.ServiceException;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;

public interface OSSCompletedCallback<T1 extends OSSRequest, T2 extends OSSResult> {

    void onSuccess(T1 request, T2 result);

    void onFailure(T1 request, ClientException clientException, ServiceException serviceException);
}
