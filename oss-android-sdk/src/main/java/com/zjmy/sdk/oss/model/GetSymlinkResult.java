package com.zjmy.sdk.oss.model;

public class GetSymlinkResult extends OSSResult {
    private String targetObjectName;

    public String getTargetObjectName() {
        return targetObjectName;
    }

    public void setTargetObjectName(String targetObjectName) {
        this.targetObjectName = targetObjectName;
    }
}
