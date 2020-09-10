package com.utopia.logan;

import android.util.Log;
import com.utopia.upload.UploadUtil;
import com.utopia.upload.callback.FileUploadListener;
import com.utopia.upload.bean.UploadFile;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class RealSendLogRunnable extends SendLogRunnable {
    private String mUploadLogUrl = "http://localhost:3000/logupload";

    @Override
    public void sendLog(final File logFile) {
        if (logFile.isFile()) {
            UploadUtil.initFormUpload()
                    .addFile(logFile.getName(), logFile.getName(), logFile)
                    .url(mUploadLogUrl)
                    .fileUploadBuild()
                    .upload(new FileUploadListener() {
                        @Override
                        public void onFinish(String response) {
                            Log.d("test", "日志文件发送成功！！");
                            logFile.delete();
                        }

                        @Override
                        public void onError(String error) {
                            Log.e("test", "日志文件发送失败！！" + error);
                        }
                    });
        }else{
            final File[] mLogFiles = logFile.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".gz");
                }
            });

            if (mLogFiles!=null && mLogFiles.length > 0){
                List<UploadFile> uploadFiles = new ArrayList<>();
                for (File file : mLogFiles){
                    uploadFiles.add(new UploadFile(file.getName(),file.getName(),file));
                }

                UploadUtil.initFormUpload()
                        .url(mUploadLogUrl)
                        .addFiles(uploadFiles)
                        .fileUploadBuild()
                        .upload(new FileUploadListener() {
                            @Override
                            public void onFinish(String response) {
                                Log.d("test", "日志文件批量发送成功！！");
                                for (File file : mLogFiles){
                                    file.delete();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e("test", "日志文件批量发送失败！！" + error);
                            }
                        });
            }
        }

        finish();
    }

    void setUploadLogUrl(String mUploadLogUrl) {
        this.mUploadLogUrl = mUploadLogUrl;
    }
}
