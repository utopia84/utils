package com.utopia.logan;

import android.text.TextUtils;

public class LoganConfig {

    public static final long KB = 1024; //KB
    private static final long DEFAULT_FILE_SIZE = 10 * KB;
    private static final int DEFAULT_QUEUE = 500;

    String mCachePath; //mmap缓存路径
    String mPathPath; //file文件路径
    String mSerialNumber;//设备序列号
    String mUploadUrl;//文件上传地址
    public static long maxFileSzie = DEFAULT_FILE_SIZE; //上传文件大小
    long mMaxQueue = DEFAULT_QUEUE;

    byte[] mEncryptKey16; //128位aes加密Key
    byte[] mEncryptIv16; //128位aes加密IV

    boolean isValid() {
        boolean valid = false;
        if (!TextUtils.isEmpty(mCachePath) && !TextUtils.isEmpty(mPathPath) && mEncryptKey16 != null
                && mEncryptIv16 != null) {
            valid = true;
        }
        return valid;
    }

    private LoganConfig() {

    }

    private void setUploadUrl(String mUploadUrl) {
        this.mUploadUrl = mUploadUrl;
    }

    private void setSerialNumber(String mSerialNumber) {
        this.mSerialNumber = mSerialNumber;
    }

    private void setCachePath(String cachePath) {
        mCachePath = cachePath;
    }

    private void setPathPath(String pathPath) {
        mPathPath = pathPath;
    }

    private void setEncryptKey16(byte[] encryptKey16) {
        mEncryptKey16 = encryptKey16;
    }

    private void setEncryptIV16(byte[] encryptIv16) {
        mEncryptIv16 = encryptIv16;
    }

    public static final class Builder {
        String mCachePath; //mmap缓存路径
        String mPath; //file文件路径
        String mUploadUrl;//日志上传地址
        String mSerialNumber;//设备序列号，用于生成相关的文件名
        byte[] mEncryptKey16; //128位ase加密Key
        byte[] mEncryptIv16; //128位aes加密IV

        public Builder setCachePath(String cachePath) {
            mCachePath = cachePath;
            return this;
        }

        public Builder setPath(String path) {
            mPath = path;
            return this;
        }

        public Builder setEncryptKey16(byte[] encryptKey16) {
            mEncryptKey16 = encryptKey16;
            return this;
        }

        public Builder setEncryptIV16(byte[] encryptIv16) {
            mEncryptIv16 = encryptIv16;
            return this;
        }

        public Builder setSerialNumber(String s) {
            this.mSerialNumber = s;
            return this;
        }

        public Builder setUploadUrl(String mUploadUrl) {
            this.mUploadUrl = mUploadUrl;
            return this;
        }

        public LoganConfig build() {
            LoganConfig config = new LoganConfig();
            config.setCachePath(mCachePath);
            config.setPathPath(mPath);
            config.setEncryptKey16(mEncryptKey16);
            config.setEncryptIV16(mEncryptIv16);
            config.setSerialNumber(mSerialNumber);
            config.setUploadUrl(mUploadUrl);
            return config;
        }
    }
}
