package com.zjmy.sdk.oss;


public class OssConfig {
    // STS 鉴权服务器地址，使用前请参照文档 https://help.aliyun.com/document_detail/31920.html 介绍配置STS 鉴权服务器地址。
    // 或者根据工程sts_local_server目录中本地鉴权服务脚本代码启动本地STS 鉴权服务器。详情参见sts_local_server 中的脚本内容。
    private String stsServerUrl;
    // 访问的endpoint地址
    private String endpoint;
    private String bucket;
    //callback 回调地址
    private String callbackUrl;

    private OssConfig(){}

    public String getStsServerUrl() {
        return stsServerUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public static final class Builder {
        private String stsServerUrl;
        // 访问的endpoint地址
        private String endpoint;
        private String bucket;
        //callback 回调地址
        private String callbackUrl;

        public OssConfig.Builder stsServerUrl(String stsServerUrl) {
            this.stsServerUrl = stsServerUrl;
            return this;
        }

        public OssConfig.Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public OssConfig.Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public OssConfig.Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public OssConfig build() {
            OssConfig config = new OssConfig();
            config.stsServerUrl = stsServerUrl;
            config.endpoint = endpoint;
            config.bucket = bucket;
            config.callbackUrl = callbackUrl;
            return config;
        }
    }
}
