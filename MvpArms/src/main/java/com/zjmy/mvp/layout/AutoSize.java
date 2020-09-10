package com.zjmy.mvp.layout;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;

public final class AutoSize {
    private static SparseArray<DisplayMetricsInfo> mCache = new SparseArray<>();
    private static final int MODE_SHIFT = 30;
    private static final int MODE_MASK  = 0x3 << MODE_SHIFT;
    private static final int MODE_ON_WIDTH  = 1 << MODE_SHIFT;
    private static final int MODE_DEVICE_SIZE  = 2 << MODE_SHIFT;

    private AutoSize() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static void autoConvertDensityOfGlobal(Activity activity) {
        if (AutoSizeConfig.getInstance().mInitDensity != -1) {//代表已开启屏幕适配
            autoConvertDensity(activity);
        }
    }

    /**
     * 参考今日头条适配方案的核心代码, 核心在于根据当前设备的实际情况做自动计算并转换 {@link DisplayMetrics#density}、
     * {@link DisplayMetrics#scaledDensity}、{@link DisplayMetrics#densityDpi} 这三个值
     * @param activity      {@link Activity}
     * @see <a href="https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA">参考今日头条官方适配方案</a>
     */
    public static void autoConvertDensity(Activity activity) {
        Preconditions.checkNotNull(activity, "activity == null");
        Preconditions.checkMainThread();

        AutoSizeConfig config = AutoSizeConfig.getInstance();
        //是否按照宽度进行等比例适配, {@code true} 为以宽度进行等比例适配, {@code false} 为以高度进行等比例适配
        float designSizeInDp = config.isBaseOnWidth ? config.mDesignWidthInDp : config.mDesignHeightInDp;

        int key = (int)designSizeInDp & ~MODE_MASK;// 基础key值
        key = config.isBaseOnWidth ? (key | MODE_ON_WIDTH) : (key & ~MODE_ON_WIDTH);   //横竖屏规则变换，需要重新计算，2
        key = config.isBaseOnWidth ? (key | MODE_DEVICE_SIZE) : (key & ~MODE_DEVICE_SIZE);//状态栏是否计入，需要重新计算,2

        DisplayMetricsInfo displayMetricsInfo = mCache.get(key);

        float targetDensity ;
        int targetDensityDpi ;
        float targetScaledDensity ;

        if (displayMetricsInfo == null) {
            if (config.isBaseOnWidth) {
                targetDensity = config.getScreenWidth() * 1.0f / designSizeInDp;
            } else {
                targetDensity = config.getScreenHeight() * 1.0f / designSizeInDp;
            }

            float systemFontScale =  config.mInitScaledDensity * 1.0f / config.mInitDensity;

            targetScaledDensity = targetDensity * systemFontScale;
            targetDensityDpi = (int) (targetDensity * 160);

            if (mCache.size() > 4){
                mCache.clear();
            }
            mCache.put(key, new DisplayMetricsInfo(targetDensity, targetDensityDpi, targetScaledDensity));
            Log.e("test","displayMetricsInfo == null,mCacheSize="+mCache.size());
        } else {
            targetDensity = displayMetricsInfo.density;
            targetDensityDpi = displayMetricsInfo.densityDpi;
            targetScaledDensity = displayMetricsInfo.scaledDensity;
            Log.e("test","displayMetricsInfo != null,mCacheSize="+mCache.size());
        }

        setDensity(activity, targetDensity, targetDensityDpi, targetScaledDensity);
    }


    /**
     * {@link DisplayMetrics} 赋值
     *
     * @param activity      {@link Activity}
     * @param density       {@link DisplayMetrics#density}
     * @param densityDpi    {@link DisplayMetrics#densityDpi}
     * @param scaledDensity {@link DisplayMetrics#scaledDensity}
     */
    private static void setDensity(Activity activity, float density, int densityDpi, float scaledDensity) {
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        setDensity(activityDisplayMetrics, density, densityDpi, scaledDensity);

        DisplayMetrics appDisplayMetrics = activity.getApplication().getResources().getDisplayMetrics();
        setDensity(appDisplayMetrics, density, densityDpi, scaledDensity);
    }

    /**
     * 赋值
     *
     * @param displayMetrics {@link DisplayMetrics}
     * @param density        {@link DisplayMetrics#density}
     * @param densityDpi     {@link DisplayMetrics#densityDpi}
     * @param scaledDensity  {@link DisplayMetrics#scaledDensity}
     */
    private static void setDensity(DisplayMetrics displayMetrics, float density, int densityDpi, float scaledDensity) {
        displayMetrics.density = density;
        displayMetrics.densityDpi = densityDpi;
        displayMetrics.scaledDensity = scaledDensity;
    }
}
