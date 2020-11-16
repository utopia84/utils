package com.zjmy.sdk.oss.internal;

import com.zjmy.sdk.oss.model.OSSResult;

import java.io.IOException;

/**
 * Created by zhouzhuo on 11/23/15.
 */
public interface ResponseParser<T extends OSSResult> {

    public T parse(ResponseMessage response) throws IOException;
}
