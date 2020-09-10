/*
 * Copyright (c) 2018-present, 美团点评
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.utopia.logan;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;

class LoganControlCenter {

    private static LoganControlCenter sLoganControlCenter;

    private ConcurrentLinkedQueue<LoganModel> mCacheLogQueue = new ConcurrentLinkedQueue<>();
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mMaxQueue; //最大队列数
    private String mEncryptKey16;
    private String mEncryptIv16;
    private LoganThread mLoganThread;
    private String mSerialNumber;
    private String mUploadUrl;//文件上传地址

    private LoganControlCenter(LoganConfig config) {
        if (!config.isValid()) {
            throw new NullPointerException("config's param is invalid");
        }

        mPath = config.mPathPath;
        mCachePath = config.mCachePath;
        mMaxQueue = config.mMaxQueue;
        mEncryptKey16 = new String(config.mEncryptKey16);
        mEncryptIv16 = new String(config.mEncryptIv16);
        mSerialNumber = config.mSerialNumber;
        mUploadUrl = config.mUploadUrl;
        init();
    }

    private void init() {
        if (mLoganThread == null) {
            mLoganThread = new LoganThread(mCacheLogQueue, mCachePath, mPath,
                    mEncryptKey16, mEncryptIv16,mSerialNumber,mUploadUrl);
            mLoganThread.setName("logan-thread");
            mLoganThread.start();
        }
    }

    static LoganControlCenter instance(LoganConfig config) {
        if (sLoganControlCenter == null) {
            synchronized (LoganControlCenter.class) {
                if (sLoganControlCenter == null) {
                    sLoganControlCenter = new LoganControlCenter(config);
                }
            }
        }
        return sLoganControlCenter;
    }

    void write(LogType type,String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.WRITE;

        model.writeAction = new WriteAction(type,log);
        if (mCacheLogQueue.size() < mMaxQueue) {
            mCacheLogQueue.add(model);
            if (mLoganThread != null) {
                mLoganThread.notifyRun();
            }
        }
    }


    void send(boolean full) {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }

        LoganModel model = new LoganModel();
        SendAction action = new SendAction();
        action.isFull = full;
        model.action = LoganModel.Action.SEND;
        action.uploadPath = mPath;
        model.sendAction = action;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }

    void flush() {
        if (TextUtils.isEmpty(mPath)) {
            return;
        }
        LoganModel model = new LoganModel();
        model.action = LoganModel.Action.FLUSH;
        mCacheLogQueue.add(model);
        if (mLoganThread != null) {
            mLoganThread.notifyRun();
        }
    }
}
