package com.utopia.upload;

import com.utopia.upload.net.OkHttpManager;
import com.utopia.upload.upload.FormUploadBuilder;

import okhttp3.OkHttpClient;

public class UploadUtil {
    private static boolean isInit = false;
    public static void initialize(OkHttpClient okHttpClient){
        if (okHttpClient != null) {
            isInit = true;
            OkHttpManager.getInstance().setOkHttpClient(okHttpClient);
        }
    }

    //表单式文件上传
    public static FormUploadBuilder initFormUpload() {
        if (!isInit){
            throw new IllegalStateException("you should instantiate me!");
        }
        return new FormUploadBuilder();
    }
}
