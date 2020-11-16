package com.bjzjmy.demo;


import com.zjmy.sdk.oss.OssConfig;
import com.zjmy.sdk.oss.OssService;

public class Application extends android.app.Application {
    // 访问的endpoint地址
    public static final String OSS_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    //callback 测试地址
    public static final String OSS_CALLBACK_URL = "http://edu.bjzjmy.com:9159/aliyunOss/api/ops/file/callBack";
    // STS 鉴权服务器地址，使用前请参照文档 https://help.aliyun.com/document_detail/31920.html 介绍配置STS 鉴权服务器地址。
    // 或者根据工程sts_local_server目录中本地鉴权服务脚本代码启动本地STS 鉴权服务器。详情参见sts_local_server 中的脚本内容。
    //public static final String STS_SERVER_URL = "http://192.168.1.105:8989/sts/getsts";//STS 地址
    public static final String STS_SERVER_URL = "http://192.168.20.23:9006/aliyunOss/token";//STS 地址
    public static final String BUCKET_NAME = "sxreader-test-bucket";

    @Override
    public void onCreate() {
        super.onCreate();


        OssConfig config = new OssConfig.Builder()
                .bucket(BUCKET_NAME)
                .endpoint(OSS_ENDPOINT)
                .stsServerUrl(STS_SERVER_URL)
                .callbackUrl(OSS_CALLBACK_URL)
                .build();
        OssService.initOSS(this,config);
    }
}
