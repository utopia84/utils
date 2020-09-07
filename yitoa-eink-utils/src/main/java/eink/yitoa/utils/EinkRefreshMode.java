package eink.yitoa.utils;

import android.annotation.SuppressLint;
import android.view.View;
import java.lang.reflect.Method;

/**
 * Eink刷新模式
 */
public class EinkRefreshMode {
    private static final int EINK_DISPLAY_STRATEGY_ALL_FLIP_WITHOUT_LAST = 3;

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
        Class<?> rkClass = Class.forName("android.os.RkDisplayOutputManager");
        Method method = rkClass.getMethod("setEpdModeNoneArea", int.class);
        method.setAccessible(true);
        Object objectBook = rkClass.newInstance();
        method.invoke(objectBook,refreshMode);
    }

    /**
     * 英唐设备刷新模式
     * @param mode 4:全刷，132：局部刷新
     * @throws Exception 抛出反射异常
     */
    private static void setYitoaRefreshMode(int mode) throws Exception{
        Method method = View.class.getMethod("setEinkUpdateStrategy", int.class, int.class, int.class, int.class);
        method.setAccessible(true);
        method.invoke(null, EinkRefreshMode.EINK_DISPLAY_STRATEGY_ALL_FLIP_WITHOUT_LAST,
                mode,mode,mode);
    }
}
