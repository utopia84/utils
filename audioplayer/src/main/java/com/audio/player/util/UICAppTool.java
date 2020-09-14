package com.audio.player.util;

import android.app.Application;
import android.content.Context;

/**
 * @作者 邸昌顺
 * @时间 2019/3/4 13:23
 * @描述
 */
public class UICAppTool {

    private static Application mApplication;

    public static void instance(Application app) {
        mApplication = app;
    }

    public static Context getApplicationContext() {
        return mApplication;
    }

}
