package com.zjmy.mvp.layout;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.zjmy.mvp.layout.utils.AutoSizeLog;
import com.zjmy.mvp.layout.utils.Preconditions;

import androidx.appcompat.app.AppCompatActivity;
import eink.yitoa.utils.common.ScreenUtils;

/**
 * 修改系统density适配屏幕（160为基准dpi）
 * density = dpi / 160;
 * px = density * dp;
 *
 */
public class AutoSizeConfig {
    private static final String KEY_DESIGN_WIDTH_IN_DP = "design_width_in_dp";
    private static final String KEY_DESIGN_HEIGHT_IN_DP = "design_height_in_dp";

    private static volatile AutoSizeConfig sInstance;
    private Application mApplication;

    /**
     * 最初的屏幕密度 {@link DisplayMetrics#density}
     */
    private float mInitDensity = -1;

    /**
     * 最初的字体缩放比 {@link DisplayMetrics#scaledDensity}
     */
    private float mInitScaledDensity;

    /**
     * 设计图上的总宽度, 单位 dp
     */
    private int mDesignWidthInDp;
    /**
     * 设计图上的总高度, 单位 dp
     */
    private int mDesignHeightInDp;
    /**
     * 设备的屏幕总宽度, 单位 px
     */
    private int mScreenWidth;
    /**
     * 设备的屏幕总高度, 单位 px, 如果 {@link #isUseDeviceSize} 为 {@code false}, 屏幕总高度会减去状态栏的高度
     */
    private int mScreenHeight;
    /**
     * 状态栏高度, 当 {@link #isUseDeviceSize} 为 {@code false} 时, AndroidAutoSize 会将 {@link #mScreenHeight} 减去状态栏高度
     * AndroidAutoSize 默认使用 {@link ScreenUtils#getStatusBarHeight()} 方法获取状态栏高度
     */
    private int mStatusBarHeight;
    /**
     * 为了保证在不同高宽比的屏幕上显示效果也能完全一致, 所以本方案适配时是以设计图宽度与设备实际宽度的比例或设计图高度与设备实际高度的比例应用到
     * 每个 View 上 (只能在宽度和高度之中选一个作为基准), 从而使每个 View 的高和宽用同样的比例缩放, 避免在与设计图高宽比不一致的设备上出现适配的 View 高或宽变形的问题
     * {@link #isBaseOnWidth} 为 {@code true} 时代表以宽度等比例缩放, {@code false} 代表以高度等比例缩放
     * {@link #isBaseOnWidth} 为全局配置, 默认为 {@code true}, 每个 {@link AppCompatActivity} 也可以单独选择使用高或者宽做等比例缩放
     */
    private boolean isBaseOnWidth = true;

    /**
     * 此字段表示是否使用设备的实际尺寸做适配
     * {@link #isUseDeviceSize} 为 {@code true} 表示屏幕高度 {@link #mScreenHeight} 包含状态栏的高度
     * {@link #isUseDeviceSize} 为 {@code false} 表示 {@link #mScreenHeight} 会减去状态栏的高度, 默认为 {@code true}
     */
    private boolean isUseDeviceSize = true;

    public static AutoSizeConfig getInstance() {
        if (sInstance == null) {
            synchronized (AutoSizeConfig.class) {
                if (sInstance == null) {
                    sInstance = new AutoSizeConfig();
                }
            }
        }
        return sInstance;
    }

    private AutoSizeConfig() {
    }

    /**
     * 初始化方法只能调用一次, 否则报错
     * 此方法默认使用以宽度进行等比例适配, 如想使用以高度进行等比例适配, 请调用 {@link #init(Application, boolean)}
     *
     * @param application {@link Application}
     */
    @SuppressLint("RestrictedApi")
    public AutoSizeConfig init(final Application application, boolean isBaseOnWidth) {
        Preconditions.checkArgument(mInitDensity == -1, "AutoSizeConfig#init() can only be called once");
        Preconditions.checkNotNull(application, "application == null");

        this.mApplication = application;
        this.isBaseOnWidth = isBaseOnWidth;
        final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        final Configuration configuration = Resources.getSystem().getConfiguration();

        //检查是否在 Application#onCreate中调用setDesignSizeInDp配置设计图尺寸的方式
        checkDesignSizeIsLegal();

        int[] screenSize = ScreenUtils.getScreenSize(application);
        mScreenWidth = screenSize[0];
        mScreenHeight = screenSize[1];
        mStatusBarHeight = ScreenUtils.getStatusBarHeight();
        AutoSizeLog.e("designWidthInDp = " + mDesignWidthInDp + ", designHeightInDp = " + mDesignHeightInDp + ", screenWidth = " + mScreenWidth + ", screenHeight = " + mScreenHeight);

        mInitDensity = displayMetrics.density;
        mInitScaledDensity = displayMetrics.scaledDensity;

        //自动监听调用相关适配
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
        AutoSizeLog.e("initDensity = " + mInitDensity + ", initScaledDensity = " + mInitScaledDensity);
        return this;
    }


    /**
     * 设置全局设计图宽高,单位dp
     *
     * @param designWidthInDp 设计图宽度
     */
    public AutoSizeConfig setDesignSizeInDp(int designWidthInDp , int designHeightInDp){
        Preconditions.checkArgument(designWidthInDp > 0, "designWidthInDp must be > 0");
        Preconditions.checkArgument(designHeightInDp > 0, "designHeightInDp must be > 0");

        mDesignWidthInDp = designWidthInDp;
        mDesignHeightInDp = designHeightInDp;
        return this;
    }

    public void checkDesignSizeIsLegal(){
        Preconditions.checkArgument(mDesignWidthInDp > 0, "designWidthInDp must be > 0");
        Preconditions.checkArgument(mDesignHeightInDp > 0, "designHeightInDp must be > 0");
    }

    /**
     * 获取 {@link #mInitDensity}
     *
     * @return {@link #mInitDensity}
     */
    public float getInitDensity() {
        return mInitDensity;
    }

    /**
     * 是否打印 Log
     *
     * @param log {@code true} 为打印
     */
    public AutoSizeConfig setLog(boolean log) {
        AutoSizeLog.setDebug(log);
        return this;
    }

    /**
     * 是否使用设备的实际尺寸做适配
     *
     * @param useDeviceSize {@code true} 为使用设备的实际尺寸 (包含状态栏), {@code false} 为不使用设备的实际尺寸 (不包含状态栏)
     * @see #isUseDeviceSize 详情请查看这个字段的注释
     */
    public AutoSizeConfig setUseDeviceSize(boolean useDeviceSize) {
        isUseDeviceSize = useDeviceSize;
        return this;
    }

    /**
     * 返回 {@link #isUseDeviceSize}
     *
     * @return {@link #isUseDeviceSize}
     */
    public boolean isUseDeviceSize() {
        return isUseDeviceSize;
    }

    /**
     * 返回 {@link #mScreenWidth}
     *
     * @return {@link #mScreenWidth}
     */
    public int getScreenWidth() {
        return mScreenWidth;
    }

    /**
     * 返回 {@link #mScreenHeight}
     *
     * @return {@link #mScreenHeight}
     */
    public int getScreenHeight() {
        return isUseDeviceSize() ? mScreenHeight : mScreenHeight - mStatusBarHeight;
    }

    /**
     * 返回 {@link #isBaseOnWidth}
     *
     * @return {@link #isBaseOnWidth}
     */
    public boolean isBaseOnWidth() {
        return isBaseOnWidth;
    }

    /**
     * 获取 {@link #mDesignWidthInDp}
     *
     * @return {@link #mDesignWidthInDp}
     */
    public int getDesignWidthInDp() {
        Preconditions.checkArgument(mDesignWidthInDp > 0, "you must set " + KEY_DESIGN_WIDTH_IN_DP + "  in your AndroidManifest file");
        return mDesignWidthInDp;
    }

    /**
     * 获取 {@link #mDesignHeightInDp}
     *
     * @return {@link #mDesignHeightInDp}
     */
    public int getDesignHeightInDp() {
        Preconditions.checkArgument(mDesignHeightInDp > 0, "you must set " + KEY_DESIGN_HEIGHT_IN_DP + "  in your AndroidManifest file");
        return mDesignHeightInDp;
    }

    public Application getApplication(){
        return mApplication;
    }

    /**
     * 获取 {@link #mInitScaledDensity}
     *
     * @return {@link #mInitScaledDensity}
     */
    public float getInitScaledDensity() {
        return mInitScaledDensity;
    }
}
