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
import java.io.FilenameFilter;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class LoganThread extends Thread {

    private static final String TAG = "LoganThread";

    private final Object sync = new Object();
    private final Object sendSync = new Object();
    private volatile boolean mIsRun = true;

    private boolean mIsWorking;

    private LoganProtocol mLoganProtocol;
    private ConcurrentLinkedQueue<LoganModel> mCacheLogQueue;
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private String mSerialNumber;//设备序列号，用于生成相关的文件名

    private String mEncryptKey16;
    private String mEncryptIv16;
    private int mSendLogStatusCode;
    private String mUploadUrl;
    // 发送缓存队列
    private ConcurrentLinkedQueue<LoganModel> mCacheSendQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService mSingleThreadExecutor;

    /**
     * 日志命名的其中一部分：编号
     */
    private String funFileName;
    private String behFileName;
    private LogType oldLogType = LogType.NONE;

    private RealSendLogRunnable mSendLogRunnable = new RealSendLogRunnable();

    LoganThread(
            ConcurrentLinkedQueue<LoganModel> cacheLogQueue, String cachePath,
            String path, String encryptKey16,
            String encryptIv16, String serialNumber, String uploadUrl) {
        mCacheLogQueue = cacheLogQueue;
        mCachePath = cachePath;
        mPath = path;
        mEncryptKey16 = encryptKey16;
        mEncryptIv16 = encryptIv16;
        mSerialNumber = serialNumber;
        mUploadUrl = uploadUrl;

        //检查日志缓冲区是否存在日志
        File[] files = new File(mPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        });
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.getName().startsWith("BEH-")) {
                    behFileName = file.getName();
                } else if (file.getName().startsWith("FUN-")) {
                    funFileName = file.getName();
                }
            }
        }
    }

    void notifyRun() {
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    void quit() {
        mIsRun = false;
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        while (mIsRun) {
            synchronized (sync) {
                mIsWorking = true;
                try {
                    LoganModel model = mCacheLogQueue.poll();
                    if (model == null) {
                        mIsWorking = false;
                        sync.wait();
                        mIsWorking = true;
                    } else {
                        action(model);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    mIsWorking = false;
                }
            }
        }
    }

    private void action(LoganModel model) {
        if (model == null || !model.isValid()) {
            return;
        }
        if (mLoganProtocol == null) {
            mLoganProtocol = LoganProtocol.newInstance();
            mLoganProtocol.setOnLoganProtocolStatus(new OnLoganProtocolStatus() {
                @Override
                public void loganProtocolStatus(String cmd, int code) {
                    LogWriter.onListenerLogWriteStatus(cmd, code);
                }
            });
            mLoganProtocol.logan_init(mCachePath, mPath, mEncryptKey16,
                    mEncryptIv16);
            mLoganProtocol.logan_debug(LogWriter.sDebug);
        }

        if (model.action == LoganModel.Action.WRITE) {
            doWriteLog2File(model.writeAction);
        } else if (model.action == LoganModel.Action.SEND) {
            // 是否正在发送
            synchronized (sendSync) {
                if (mSendLogStatusCode == SendLogRunnable.SENDING) {
                    mCacheSendQueue.add(model);
                } else {
                    doSendLog2Net(model.sendAction);
                }
            }
        } else if (model.action == LoganModel.Action.FLUSH) {
            doFlushLog2File();
        }
    }

    private void doFlushLog2File() {
        if (LogWriter.sDebug) {
            Log.d(TAG, "LogWriter flush start");
        }
        if (mLoganProtocol != null) {
            mLoganProtocol.logan_flush();
        }
    }

    private void doWriteLog2File(WriteAction action) {
        checkLogFile(action.type);
        mLoganProtocol.logan_write(action.log);
        checkCacheSize(action.type);
    }

    //检查文件大小
    private void checkCacheSize(LogType logType) {
        doFlushLog2File();
        if (LogType.BEHAVIORLOG == logType) {//行为日志
            File file = new File(mPath, behFileName);
            //Log.e("test","fileSize:"+file.length());
            //Log.e("test","limitSize:"+LoganConfig.maxFileSzie);
            if (file.isFile() && file.length() >= LoganConfig.maxFileSzie) {//上传文件
                boolean success = prepareLogFile(behFileName);
                if (!success) {
                    if (LogWriter.sDebug) {
                        Log.d(TAG, "LogWriter prepare log file failed, can't find log file");
                    }
                } else {
                    behFileName = null;
                    LogWriter.s(false);
                }
            }
        } else if (LogType.FUNCTIONLOG == logType) {//功能日志
            File file = new File(mPath, funFileName);
            if (file.isFile() && file.length() >= LoganConfig.maxFileSzie) {//上传文件
                boolean success = prepareLogFile(funFileName);

                if (!success) {
                    if (LogWriter.sDebug) {
                        Log.d(TAG, "LogWriter prepare log file failed, can't find log file");
                    }
                } else {
                    funFileName = null;
                    LogWriter.s(false);
                }
            }
        }
    }

    /**
     * 发送日志前的预处理操作
     */
    private boolean prepareLogFile(String fileName) {
        if (LogWriter.sDebug) {
            Log.d(TAG, "prepare log file");
        }

        if (!TextUtils.isEmpty(fileName)) {
            File oldFile = new File(mPath, fileName);
            return oldFile.renameTo(new File(mPath, fileName + ".gz"));
        }
        return false;
    }

    private void doSendLog2Net(SendAction action) {
        if (mSendLogRunnable != null && !TextUtils.isEmpty(mUploadUrl)) {
            if (action.isFull){
                doFlushLog2File();
                behFileName = null;
                funFileName = null;
                File[] files = new File(mPath).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                });
                if (files!=null && files.length > 0){
                    for (File file : files){
                        file.renameTo((new File(mPath, file.getName() + ".gz")));
                    }
                }
            }

            mSendLogRunnable.setUploadLogUrl(mUploadUrl);
            action.sendLogRunnable = mSendLogRunnable;
            if (LogWriter.sDebug) {
                Log.d(TAG, "LogWriter send start");
            }
            if (TextUtils.isEmpty(mPath) || TextUtils.isEmpty(mUploadUrl) || !action.isValid()) {
                return;
            }


            action.sendLogRunnable.setSendAction(action);
            action.sendLogRunnable.setCallBackListener(
                    new SendLogRunnable.OnSendLogCallBackListener() {
                        @Override
                        public void onCallBack(int statusCode) {
                            synchronized (sendSync) {
                                mSendLogStatusCode = statusCode;
                                if (statusCode == SendLogRunnable.FINISH) {
                                    mCacheLogQueue.addAll(mCacheSendQueue);
                                    mCacheSendQueue.clear();
                                    notifyRun();
                                }
                            }
                        }
                    });
            mSendLogStatusCode = SendLogRunnable.SENDING;
            if (mSingleThreadExecutor == null) {
                mSingleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        // Just rename Thread
                        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                                "logan-thread-send-log", 0);
                        if (t.isDaemon()) {
                            t.setDaemon(false);
                        }
                        if (t.getPriority() != Thread.NORM_PRIORITY) {
                            t.setPriority(Thread.NORM_PRIORITY);
                        }
                        return t;
                    }
                });
            }
            mSingleThreadExecutor.execute(action.sendLogRunnable);
        }
    }

    private void checkLogFile(LogType type) {
        boolean isOpenFile = false;
        String fileName = "";
        if (LogType.BEHAVIORLOG == type) {
            if (TextUtils.isEmpty(behFileName) || !(new File(mPath, behFileName)).exists()) {
                behFileName = "BEH-" + mSerialNumber + "-" + System.currentTimeMillis() + ".txt";
                openLogFile(behFileName);
                isOpenFile = true;
            }
            fileName = behFileName;
        } else if (LogType.FUNCTIONLOG == type) {
            if (TextUtils.isEmpty(funFileName) || !(new File(mPath, funFileName)).exists()) {
                funFileName = "FUN-" + mSerialNumber + "-" + System.currentTimeMillis() + ".txt";
                openLogFile(funFileName);
                isOpenFile = true;
            }
            fileName = funFileName;
        }

        if (!isOpenFile) {
            if (oldLogType != type) {
                openLogFile(fileName);
            }
        }

        oldLogType = type;
    }

    /**
     * 打开需要读写的文件
     *
     * @param fileName
     */
    private void openLogFile(String fileName) {
        mLoganProtocol.logan_flush();
        mLoganProtocol.logan_open(fileName);
    }
}
