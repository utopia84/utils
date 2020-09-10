package com.zjmy.mvp.layout;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import eink.yitoa.utils.common.ScreenUtils;

/**
 * 修改系统density适配屏幕（160为基准dpi）
 * density = dpi / 160;
 * px = density * dp;
 *
 * AutoSizeConfig.getInstance().setDesignSizeInDp(1080,1920).setUseDeviceSize(false).init(this);
 */
public class AutoSizeConfig {

    private static volatile AutoSizeConfig sInstance;

    /**
     * 最初的屏幕密度 {@link DisplayMetrics#density}
     */
    protected float mInitDensity = -1;

    /**
     * 最初的字体缩放比 {@link DisplayMetrics#scaledDensity}
     */
    protected float mInitScaledDensity;

    /**
     * 设计图上的总宽度, 单位 dp
     */
    protected int mDesignWidthInDp;
    /**
     * 设计图上的总高度, 单位 dp
     */
    protected int mDesignHeightInDp;
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
     * AndroidAutoSize 默认使用 getStatusBarHeight 方法获取状态栏高度
     */
    protected int mStatusBarHeight;
    /**
     * 为了保证在不同高宽比的屏幕上显示效果也能完全一致, 所以本方案适配时是以设计图宽度与设备实际宽度的比例或设计图高度与设备实际高度的比例应用到
     * 每个 View 上 (只能在宽度和高度之中选一个作为基准), 从而使每个 View 的高和宽用同样的比例缩放, 避免在与设计图高宽比不一致的设备上出现适配的 View 高或宽变形的问题
     * {isBaseOnWidth} 为 {@code true} 时代表以宽度等比例缩放, {@code false} 代表以高度等比例缩放
     * {isBaseOnWidth} 为全局配置, 默认为 {@code true}, 每个 {@link AppCompatActivity} 也可以单独选择使用高或者宽做等比例缩放
     */
    protected boolean isBaseOnWidth = true;

    /**
     * 此字段表示是否使用设备的实际尺寸做适配
     * {isUseDeviceSize} 为 {@code true} 表示屏幕高度 {@link #mScreenHeight} 包含状态栏的高度
     * {isUseDeviceSize} 为 {@code false} 表示 {@link #mScreenHeight} 会减去状态栏的高度, 默认为 {@code true}
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
     * 此方法默认使用以宽度进行等比例适配}
     *
     * @param application {@link Application}
     */
    @SuppressLint("RestrictedApi")
    public void init(final Application application) {
        Preconditions.checkArgument(mInitDensity == -1, "AutoSizeConfig#init() can only be called once");
        Preconditions.checkNotNull(application, "application == null");

        final DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        //检查是否在 Application#onCreate中调用setDesignSizeInDp配置设计图尺寸的方式
        Preconditions.checkArgument(mDesignWidthInDp > 0 && mDesignHeightInDp > 0, "designSize must be set");

        int[] screenSize = ScreenUtils.getScreenSize(application);
        mScreenWidth = screenSize[0];
        mScreenHeight = screenSize[1];
        mStatusBarHeight = ScreenUtils.getStatusBarHeight();
        Log.e("test","designWidthInDp = " + mDesignWidthInDp + ", designHeightInDp = " + mDesignHeightInDp + ", screenWidth = " + mScreenWidth + ", screenHeight = " + mScreenHeight);

        mInitDensity = displayMetrics.density;
        mInitScaledDensity = displayMetrics.scaledDensity;

        //自动监听调用相关适配
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
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

    /**
     * 是否全局按照宽度进行等比例适配
     *
     * @param baseOnWidth {@code true} 为按照宽度, {@code false} 为按照高度
     * @see #isBaseOnWidth 详情请查看这个字段的注释
     */
    public AutoSizeConfig setBaseOnWidth(boolean baseOnWidth) {
        isBaseOnWidth = baseOnWidth;
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

    public int getScreenHeight() {
        return isUseDeviceSize ? mScreenHeight : mScreenHeight - mStatusBarHeight;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }
}
