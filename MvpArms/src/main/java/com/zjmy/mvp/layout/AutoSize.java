package com.zjmy.mvp.layout;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import com.zjmy.mvp.layout.utils.AutoSizeLog;
import com.zjmy.mvp.layout.utils.Preconditions;
import java.util.Locale;

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
        AutoSizeConfig config = AutoSizeConfig.getInstance();
        if (config.isBaseOnWidth()) {//以宽度为基准进行适配
            autoConvertDensity(activity, config.getDesignWidthInDp(), true);
        } else {//以高度为基准进行适配
            autoConvertDensity(activity, config.getDesignHeightInDp(), false);
        }
    }

    /**
     * 使用今日头条适配方案的核心代码, 核心在于根据当前设备的实际情况做自动计算并转换 {@link DisplayMetrics#density}、
     * {@link DisplayMetrics#scaledDensity}、{@link DisplayMetrics#densityDpi} 这三个值
     *
     * @param activity      {@link Activity}
     * @param sizeInDp      设计图上的设计尺寸, 单位 dp, 如果 {@param isBaseOnWidth} 设置为 {@code true},
     *                      {@param sizeInDp} 则应该填写设计图的总宽度, 如果 {@param isBaseOnWidth} 设置为 {@code false},
     *                      {@param sizeInDp} 则应该填写设计图的总高度
     * @param isBaseOnWidth 是否按照宽度进行等比例适配, {@code true} 为以宽度进行等比例适配, {@code false} 为以高度进行等比例适配
     * @see <a href="https://mp.weixin.qq.com/s/d9QCoBP6kV9VSWvVldVVwA">今日头条官方适配方案</a>
     */
    public static void autoConvertDensity(Activity activity, float sizeInDp, boolean isBaseOnWidth) {
        Preconditions.checkNotNull(activity, "activity == null");
        Preconditions.checkMainThread();

        float subunitsDesignSize = isBaseOnWidth ? AutoSizeConfig.getInstance().getDesignWidthInDp()
                : AutoSizeConfig.getInstance().getDesignHeightInDp();
        subunitsDesignSize = subunitsDesignSize > 0 ? subunitsDesignSize : sizeInDp;

        int screenSize = isBaseOnWidth ? AutoSizeConfig.getInstance().getScreenWidth()
                : AutoSizeConfig.getInstance().getScreenHeight();

        int key = Math.round((sizeInDp + subunitsDesignSize + screenSize) * AutoSizeConfig.getInstance().getInitScaledDensity()) & ~MODE_MASK;
        key = isBaseOnWidth ? (key | MODE_ON_WIDTH) : (key & ~MODE_ON_WIDTH);
        key = AutoSizeConfig.getInstance().isUseDeviceSize() ? (key | MODE_DEVICE_SIZE) : (key & ~MODE_DEVICE_SIZE);

        DisplayMetricsInfo displayMetricsInfo = mCache.get(key);

        float targetDensity ;
        int targetDensityDpi ;
        float targetScaledDensity ;

        if (displayMetricsInfo == null) {
            if (isBaseOnWidth) {
                targetDensity = AutoSizeConfig.getInstance().getScreenWidth() * 1.0f / sizeInDp;
            } else {
                targetDensity = AutoSizeConfig.getInstance().getScreenHeight() * 1.0f / sizeInDp;
            }

            float systemFontScale =  AutoSizeConfig.getInstance().
                    getInitScaledDensity() * 1.0f / AutoSizeConfig.getInstance().getInitDensity();
            targetScaledDensity = targetDensity * systemFontScale;

            targetDensityDpi = (int) (targetDensity * 160);


            mCache.put(key, new DisplayMetricsInfo(targetDensity, targetDensityDpi, targetScaledDensity));
        } else {
            targetDensity = displayMetricsInfo.getDensity();
            targetDensityDpi = displayMetricsInfo.getDensityDpi();
            targetScaledDensity = displayMetricsInfo.getScaledDensity();
        }

        setDensity(activity, targetDensity, targetDensityDpi, targetScaledDensity);

        AutoSizeLog.e(String.format(Locale.ENGLISH, "The %s has been adapted! \n%s Info: isBaseOnWidth = %s, %s = %f, %s = %f, targetDensity = %f, targetScaledDensity = %f, targetDensityDpi = %d"
                , activity.getClass().getName(), activity.getClass().getSimpleName(), isBaseOnWidth, isBaseOnWidth ? "designWidthInDp"
                        : "designHeightInDp", sizeInDp, isBaseOnWidth ? "designWidthInSubunits" : "designHeightInSubunits", subunitsDesignSize
                , targetDensity, targetScaledDensity, targetDensityDpi));
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

        DisplayMetrics appDisplayMetrics = AutoSizeConfig.getInstance().getApplication().getResources().getDisplayMetrics();
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
