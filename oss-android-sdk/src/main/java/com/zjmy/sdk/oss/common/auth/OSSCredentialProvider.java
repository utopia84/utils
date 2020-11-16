package com.zjmy.sdk.oss.common.auth;

import com.zjmy.sdk.oss.exception.ClientException;

/**
 * Created by zhouzhuo on 11/4/15.
 */
public interface OSSCredentialProvider {

    /**
     * get OSSFederationToken instance
     *
     * @return
     */
    OSSFederationToken getFederationToken() throws ClientException;
}
