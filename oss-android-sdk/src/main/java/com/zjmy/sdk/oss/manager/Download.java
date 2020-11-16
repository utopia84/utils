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
 * @date 2020/11/16 15:52
 */
public interface Download {
    void download(String objectName, String localFilePath);

    void download(String objectName, String localFilePath , SimpleOSSCallBack callback );
}
