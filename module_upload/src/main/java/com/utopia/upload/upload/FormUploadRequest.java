package com.utopia.upload.upload;

import com.utopia.upload.upload.base.BaseUploadRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public abstract class FormUploadRequest extends BaseUploadRequest {

    @Override
    protected RequestBody initRequestBody() {
        RequestBody requestBody;

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (params != null && params.size() > 0) {
            for(Map.Entry<String, String> entry : params.entrySet()){
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        buildRequestBody(builder);

        requestBody = builder.build();

        return requestBody;
    }

    protected abstract void buildRequestBody(MultipartBody.Builder builder);
}
