package eink.yitoa.utils.common;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 获取屏幕尺寸相关
 */
public class ScreenUtils {

    private ScreenUtils() {
        throw new IllegalStateException("you can't instantiate me!");
    }

    public static int getStatusBarHeight() {
        int result = 0;
        try {
            int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = Resources.getSystem().getDimensionPixelSize(resourceId);
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取当前的屏幕尺寸
     *
     * @param context {@link Context}
     * @return 屏幕尺寸
     */
    public static int[] getScreenSize(Context context) {
        int[] size = new int[]{0,0};
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (w!=null) {
            Display d = w.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            d.getMetrics(metrics);

            size[0] = metrics.widthPixels;
            size[1] = metrics.heightPixels;
        }
        return size;
    }
}
