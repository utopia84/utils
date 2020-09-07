package eink.yitoa.utils.common;

import android.app.Application;

public class ApplicationUtils {

    public static Application getApplication(){
        Application application = getApplicationFromActivityThread();
        if (application == null) {
            application = getApplicationFromAppGlobals();
        }
        return application;
    }

    private static Application getApplicationFromActivityThread(){
        Application application = null;
        try{
            application = (Application)ReflectUtils.reflect("android.app.ActivityThread")
                    .method("currentApplication")
                    .invoke();
        }catch (Exception ignored){

        }

        return application;
    }

    private static Application getApplicationFromAppGlobals(){
        Application application = null;
        try {
            application = (Application)ReflectUtils.reflect("android.app.AppGlobals")
                    .method("getInitialApplication")
                    .invoke();
        } catch (Exception ignored) {
        }
        return application;
    }
}
