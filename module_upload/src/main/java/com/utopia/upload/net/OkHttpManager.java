package com.utopia.upload.net;

import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpManager {
    private OkHttpClient okHttpClient;

    public static OkHttpManager getInstance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public void initRequest(String url, RequestBody requestBody, Map<String, String> headers, final Callback callback) {
        if (okHttpClient != null) {
            //构建http post请求
            Request.Builder requestBuilder = new Request.Builder()
                    .url(url)
                    .post(requestBody);

            //添加请求头
            if (headers != null && !headers.isEmpty()) {
                Headers.Builder headerBuilder = new Headers.Builder();
                for(Map.Entry<String, String> entry : headers.entrySet()){
                    headerBuilder.add(entry.getKey(), entry.getValue());
                }
                requestBuilder.headers(headerBuilder.build());
            }

            //发起上传请求
            okHttpClient.newCall(requestBuilder.build()).enqueue(callback);
        }
    }
}
