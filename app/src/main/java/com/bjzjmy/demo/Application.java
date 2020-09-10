package com.bjzjmy.demo;

import com.zjmy.mvp.layout.AutoSizeConfig;

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AutoSizeConfig.getInstance()
                .setDesignSizeInDp(600,800)
                .setUseDeviceSize(true)
                .init(this);
    }
}
