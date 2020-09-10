package com.bjzjmy.demo;

import com.utopia.logan.LogWriter;
import com.utopia.logan.LoganConfig;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LoganConfig config = new LoganConfig.Builder()
                .setCachePath(getFilesDir().getAbsolutePath())
                .setPath(getFilesDir().getAbsolutePath())
                .setUploadUrl("http://localhost:3000/logupload")
                .setSerialNumber("123456789")
                .setEncryptKey16("0123456789012345".getBytes())
                .setEncryptIV16("0123456789012345".getBytes())
                .build();
        LogWriter.init(config);

    }
}
