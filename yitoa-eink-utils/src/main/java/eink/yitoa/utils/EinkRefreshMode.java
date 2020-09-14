package eink.yitoa.utils;

import android.annotation.SuppressLint;
import android.view.View;

import eink.yitoa.utils.common.ReflectUtils;

/**
 * Eink刷新模式
 */
public class EinkRefreshMode {
    private EinkRefreshMode() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 局部刷新
     */
    public static void updateToLocalRefreshMode() {
        try {
            setYitoaRefreshMode(132);
        } catch (Exception e) {
            try {
                setGuowenRefreshMode(3);
            } catch (Exception ignored) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * 全局刷新
     */
    public static void updateToFullRefreshMode() {
        try {
            setYitoaRefreshMode(4);
        } catch (Exception e) {
            try {
                setGuowenRefreshMode(1);
            } catch (Exception ignored) {
                //ignored.printStackTrace();
            }
        }
    }

    /**
     * 国文设备刷新模式
     * @param refreshMode 1:全刷，3:局部刷新
     */
    @SuppressLint("PrivateApi")
    private static void setGuowenRefreshMode(int refreshMode) throws Exception {
        ReflectUtils.reflect("android.os.RkDisplayOutputManager")
                .method("setEpdModeNoneArea", int.class)
                .newInstance()
                .invoke(refreshMode);
    }

    /**
     * 英唐设备刷新模式
     * @param mode 4:全刷，132：局部刷新
     * @throws Exception 抛出反射异常
     */
    private static void setYitoaRefreshMode(int mode) throws Exception{
        ReflectUtils.reflect(View.class)
                .method("setEinkUpdateStrategy", int.class, int.class, int.class, int.class)
                .invoke(3,mode,mode,mode);//3表示只刷最后一帧
    }
}
