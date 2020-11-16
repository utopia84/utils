package com.zjmy.sdk.oss.model;

public class GetBucketRefererRequest extends OSSRequest {
    private String mBucketName;

    public String getBucketName() {
        return mBucketName;
    }

    public void setBucketName(String bucketName) {
        this.mBucketName = bucketName;
    }
}
