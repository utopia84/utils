package me.wcy.htmltext;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UICDisplayTool {

    private static final String ACTIVITY_THREAD_CLASS_NAME = "android.app.ActivityThread";
    private static final String ACTIVITY_THREAD_METHOD_NAME = "currentActivityThread";
    private static final String APPLICATION_METHOD_NAME = "getApplication";

    private static DisplayMetrics mDisplayMetrics;
    private static Application sApplication;

    //使用前，需要初始化；最好是在应用Application中做初始化
    public static void instance(Application context){
        UICDisplayTool.sApplication = context;
        if(mDisplayMetrics == null){
            mDisplayMetrics = getApplicationByReflect().getResources().getDisplayMetrics();
        }
    }

    public static Application getApplication(){
        return sApplication;
    }

    public static float getDensity(){
        return mDisplayMetrics.density;
    }

    public static int dp2Px(float dpValue){
        return (int) (mDisplayMetrics.density*dpValue + 0.5f);
    }

    public static int px2Dp(float pxValue){
        return (int) (pxValue/mDisplayMetrics.density + 0.5f);
    }

    public static int sp2Px(float spValue){
        return (int) (spValue*mDisplayMetrics.scaledDensity + 0.5f);
    }

    public static int px2Sp(float pxValue){
        return (int) (pxValue/mDisplayMetrics.scaledDensity + 0.5f);
    }

    private static Application getApplicationByReflect(){
        Application app = null;
        Object activityThread;
        try {
            Class acThreadClass = Class.forName(ACTIVITY_THREAD_CLASS_NAME);
            Method acThreadMethod = acThreadClass.getMethod(ACTIVITY_THREAD_METHOD_NAME);
            acThreadMethod.setAccessible(true);
            activityThread = acThreadMethod.invoke(null);
            Method applicationMethod = activityThread.getClass().getMethod(APPLICATION_METHOD_NAME);
            app = (Application)applicationMethod.invoke(activityThread);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return app;
    }
}
