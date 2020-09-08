package com.bjzjmy.demo;

import com.zjmy.mvp.layout.AutoSizeConfig;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AutoSizeConfig.getInstance()
                .setDesignSizeInDp(1080,1920)
                .setUseDeviceSize(false)
                .setBaseOnWidth(true)
                .init(this);
    }
}
