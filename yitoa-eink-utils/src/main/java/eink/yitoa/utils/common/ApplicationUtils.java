package eink.yitoa.utils.common;

import android.app.Application;

public class ApplicationUtils {

    /**
     * 尝试反射获取application实例
     * @return Application
     */
    public static Application getApplication(){
        Application application = getApplicationFromActivityThread();

        if (application == null) {
            application = getApplicationFromAppGlobals();
        }

        if (application == null) {
            throw new NullPointerException("you should init first");
        }
        return application;
    }

    private static Application getApplicationFromAppGlobals(){
        Application application = null;
        try {
            application = (Application)ReflectUtils.reflect("android.app.AppGlobals")
                    .method("getInitialApplication")
                    .invoke()
                    .getResult();
        } catch (Exception ignored) {
        }
        return application;
    }

    private static Application getApplicationFromActivityThread(){
        Application application = null;
        try {
            application = (Application)ReflectUtils.reflect("android.app.ActivityThread")
                    .method("currentActivityThread")
                    .invoke()
                    .method("getApplication")
                    .invoke()
                    .getResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return application;
    }
}
