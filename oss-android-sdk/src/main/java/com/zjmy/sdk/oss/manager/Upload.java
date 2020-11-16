package com.zjmy.sdk.oss.manager;

import com.zjmy.sdk.oss.callback.OSSCompletedCallback;
import com.zjmy.sdk.oss.callback.OSSProgressCallback;
import com.zjmy.sdk.oss.callback.SimpleOSSCallBack;
import com.zjmy.sdk.oss.model.OSSRequest;
import com.zjmy.sdk.oss.model.OSSResult;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 16:27
 */
public interface Upload {

    void upload(String objectName, String localFilePath);

    void upload(String objectName, String localFilePath , SimpleOSSCallBack callback );
}
