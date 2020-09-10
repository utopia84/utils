package com.utopia.upload;

import com.utopia.upload.upload.FormUploadBuilder;

import okhttp3.OkHttpClient;

public class UploadUtil {
    //表单式文件上传
    public static FormUploadBuilder initFormUpload() {
        return new FormUploadBuilder();
    }
}
