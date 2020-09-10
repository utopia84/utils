package com.bjzjmy.network;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpManager {
    private OkHttpClient okHttpClient;

    public static OkHttpManager getInstance() {
        return OkHttpHolder.instance;
    }

    private static class OkHttpHolder {
        private static final OkHttpManager instance = new OkHttpManager();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private OkHttpManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory());
        }
        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
        builder.cookieJar(LocalCookieJar.getInstance());   //为OkHttp设置自动携带Cookie的功能

        builder.addInterceptor(new HttpLoggingInterceptor(message -> {
            Log.e("Interceptor -> ", message);
        }).setLevel(HttpLoggingInterceptor.Level.BODY)); //加日志

        builder.connectTimeout(30, TimeUnit.SECONDS);//设置超时
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS); //20
        //builder.connectionPool(new ConnectionPool(5,1,TimeUnit.SECONDS));
        builder.retryOnConnectionFailure(true);//错误重连

        okHttpClient = builder.build();
    }
}
