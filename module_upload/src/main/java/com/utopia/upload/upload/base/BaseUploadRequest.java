package com.utopia.upload.upload.base;

import com.bjzjmy.network.OkHttpManager;
import com.utopia.upload.callback.FileUploadListener;
import com.utopia.upload.upload.ProgressRequestBody;
import java.io.IOException;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class BaseUploadRequest {
    protected String url;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    public void upload(final FileUploadListener callback) {
        RequestBody requestBody = initRequestBody();
        requestBody = new ProgressRequestBody(requestBody,callback);

        //构建http post请求
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);

        //添加请求头
        if (headers != null && !headers.isEmpty()) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
            requestBuilder.headers(headerBuilder.build());
        }

        //发起上传请求
        Request realRequest = requestBuilder.build();
        OkHttpManager.getInstance().getOkHttpClient().newCall(realRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call,@NonNull IOException e) {
                if (callback != null) {
                    callback.onError(e.toString());
                }
            }

            @Override
            public void onResponse(@NonNull Call call,@NonNull  Response response) {
                if (response.isSuccessful() && callback != null) {
                    callback.onFinish("upload success!");
                }
            }
        });
    }

    protected abstract RequestBody initRequestBody();

}
