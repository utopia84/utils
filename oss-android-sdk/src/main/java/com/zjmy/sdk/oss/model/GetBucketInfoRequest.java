package com.zjmy.sdk.oss.model;

public class GetBucketInfoRequest extends OSSRequest {

    private String bucketName;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public GetBucketInfoRequest(String bucketName) {
        this.bucketName = bucketName;
    }
}
