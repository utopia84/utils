package eink.yitoa.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

public class SleepImageUtils {

    /**
     * 必须png
     *
     * @param context 上下文
     * @param srcBitmap 目标bitmap
     */
    public static void setSleepImage(Context context, Bitmap srcBitmap, String fileName) {
        boolean success = false;

        if (srcBitmap.getHeight() > srcBitmap.getWidth()) {
            srcBitmap = BitmapUtils.rotateBmp(srcBitmap, 270);
        }

        Point point = getScreenResolution(context);
        if (point != null) {
            if (srcBitmap.getWidth() != point.x || srcBitmap.getHeight() != point.y) {
                srcBitmap = Bitmap.createScaledBitmap(srcBitmap, point.y, point.x, true);
            }
            //save file to target directory
            String targetDir = "/data/local/assets/images";
            String targetPathString = "standby-1.png";//file name format: standby-{num}.png, num starts from 1
            if (fileName.toLowerCase().contains("bmp")) {
                success = BitmapUtils.saveBitmapToFile(srcBitmap, targetDir, targetPathString, true);
            } else if (fileName.toLowerCase().contains("png")) {
                success = BitmapUtils.savePngToFile(srcBitmap, targetDir, "standby-1.png", true);
            }

            //send broadcast
            if (success) {
                try {
                    BitmapUtils.copyFileByChannels(new File(targetDir, targetPathString), new File(targetDir, "standby-2.png"));
                    BitmapUtils.copyFileByChannels(new File(targetDir, targetPathString), new File(targetDir, "standby-3.png"));

                    Intent intent = new Intent("update_standby_pic");
                    context.sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }
        }
        Toast.makeText(context, success ? "待机壁纸设置完成" : "操作失败", Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取屏幕像素
     * @param context 上下文
     * @return 屏幕宽高，封装成Point的形式传回来
     */
    private static Point getScreenResolution(final Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;

            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(display, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            }catch (Exception ignore){

            }

            return new Point(widthPixels, heightPixels);
        }
        return null;
    }
}
