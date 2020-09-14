package eink.yitoa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eink.yitoa.utils.common.ApplicationUtils;

public class SystemUtil {
    private Context mContext;

    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String LOG_TAG = "SystemUtil";
    private static final String YITOA_PRODUCT_SN = "yitoa.product.sn";
    private final static String HAVE_BLUETOOTH = "yitoa.have.bluetooth";
    private final static String HAVE_FINGERPRINT = "yitoa.have.fingerprint";

    public static int[] sleepImageArray = {
            android.R.drawable.sleep_bg1,
            android.R.drawable.sleep_bg2,
            android.R.drawable.sleep_bg3,
            android.R.drawable.sleep_bg4,
    };
    public static int[] shutdownImageArray = {
            android.R.drawable.power_off_bg1,
            android.R.drawable.power_off_bg2,
            android.R.drawable.power_off_bg3,
            android.R.drawable.power_off_bg4,
    };

    public static String[] sleepImageArrayEx =
            {
                    "/system/media/sleepImage/sleep_bg1.png",
                    "/system/media/sleepImage/sleep_bg2.png",
                    "/system/media/sleepImage/sleep_bg3.png",
                    "/system/media/sleepImage/sleep_bg4.png",
            };

    public static String[] shutdownImageArrayEx =
            {
                    "/system/media/shutdownImage/power_off_bg1.png",
                    "/system/media/shutdownImage/power_off_bg2.png",
                    "/system/media/shutdownImage/power_off_bg3.png",
                    "/system/media/shutdownImage/power_off_bg4.png",
            };

    private SystemPresenter mSystemPresenter;
    @SuppressLint("StaticFieldLeak")
    private volatile static SystemUtil systemUtil;

    public static SystemUtil getInstance() {
        if (systemUtil == null) {
            synchronized (SystemUtil.class) {
                if (systemUtil == null) {
                    systemUtil = new SystemUtil(ApplicationUtils.getApplication());
                }
            }
        }
        return systemUtil;
    }

    private SystemUtil() {

    }

    private SystemUtil(Context context) {
        mContext = context;
        mSystemPresenter = new SystemPresenter(context);
        initBringhtness();
    }

    private String getFormattedKernelVersion() {
        String procVersionStr;

        try {
            procVersionStr = readLine(FILENAME_PROC_VERSION);

            final String PROC_VERSION_REGEX = "\\w+\\s+" + /* ignore: Linux */
                    "\\w+\\s+" + /* ignore: version */
                    "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                    "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /*
             * group 2:
             * (xxxxxx@xxxxx
             * .constant)
             */
                    "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                    "([^\\s]+)\\s+" + /* group 3: #26 */
                    "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                    "(.+)"; /* group 4: date */

            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);

            if (!m.matches()) {
                Log.e(LOG_TAG, "Regex did not match on /proc/version: "
                        + procVersionStr);
                return "Unavailable";
            } else if (m.groupCount() < 4) {
                Log.e(LOG_TAG, "Regex match on /proc/version only returned "
                        + m.groupCount() + " groups");
                return "Unavailable";
            } else {
                return m.group(1) + "\n" +
                        m.group(2) + " " + m.group(3) +
                        "\n" + m.group(4);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,
                    "IO Exception when getting kernel version for Device Info screen",
                    e);

            return "Unavailable";
        }
    }


    /**
     * Reads a line from the specified file.
     *
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    private String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename),
                256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public String getSystemProperty(String key) {
        return SystemProperties.get(key);
    }


    private void initBringhtness() {
        int level = mSystemPresenter.initBringhtness_level();
        if (level < 0 || level >= 30) {
            level = 0;
        }

        try {
            mSystemPresenter.setBringhtness(level);
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.SYS_BRIGHTNESS_LEVEL, level);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /*
     *
     */
    public void setBringhtness(int bringhtness_level) {
        try {
            if (bringhtness_level >= 0 && bringhtness_level < 30) {
                mSystemPresenter.setBringhtness(bringhtness_level);
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_BRIGHTNESS_LEVEL, bringhtness_level);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBringhtness() {
        return mSystemPresenter.initBringhtness_level();
    }


    public void reboot() {
        try {
            Intent BOOXintent = new Intent("onyx.android.show.global.dialog");
            BOOXintent.putExtra("FORCE_REBOOT", true);
            mContext.sendBroadcast(BOOXintent);


            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.putExtra("nowait", 1);
            intent.putExtra("interval", 1);
            intent.putExtra("window", 0);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "无法执行重启操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }

    public void shutdown() {
        try {
            Intent BOOXintent = new Intent("onyx.android.show.global.dialog");
            BOOXintent.putExtra("FORCE_SHUTDOWN", true);
            mContext.sendBroadcast(BOOXintent);

            Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            //其中false换成true,会弹出是否关机的确认窗口
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

            mSystemPresenter.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行关机操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }


    public void setSystemSleepImage(int imageNo) {
        try {
            if (imageNo >= 0) {
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.SYS_SLEEP_IMAGE, imageNo);

                if ("C68".equals(android.os.Build.MODEL) || "C68".equals(android.os.Build.PRODUCT)) {
                    String fileName = "standby-" + imageNo + ".png";
                    InputStream in = mContext.getResources().getAssets().open(fileName);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    SleepImageUtils.setSleepImage(mContext, imageBitmap, fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *	set sleep image number
     */
    public void setSystemSleepImage(String fileName) {
        try {
            if (!TextUtils.isEmpty(fileName)) {
                Matcher m = Pattern.compile("[^0-9]").matcher(fileName);
                String strNo = m.replaceAll("").trim();
                int imageNo = Integer.parseInt(strNo) - 1;
                Settings.System.putInt(mContext.getContentResolver(), Settings.System.SYS_SLEEP_IMAGE, imageNo);

                if ("C68".equals(android.os.Build.MODEL) || "C68".equals(android.os.Build.PRODUCT)) {
                    String sleepFileName = "standby-" + imageNo + ".png";
                    InputStream in = mContext.getResources().getAssets().open(sleepFileName);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    SleepImageUtils.setSleepImage(mContext, imageBitmap, sleepFileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }


    public int getSystemSleepImageNo() {
        int imageno = 0;
        try {
            imageno = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SYS_SLEEP_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageno;
    }


    public int[] getSleepImageArray() {
        return sleepImageArray;
    }


    public String[] getSleepImageArrayEx() {
        return sleepImageArrayEx;
    }


    public void setSystemShutdownImage(String fileName) {
        try {
            if (!TextUtils.isEmpty(fileName)) {
                Matcher m = Pattern.compile("[^0-9]").matcher(fileName);
                String strNo = m.replaceAll("").trim();
                int imageNo = Integer.parseInt(strNo) - 1;
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SHUTDOWN_IMAGE, imageNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }


    /*
     *	set shutdown image number
     */
    public void setSystemShutdownImage(int imageNo) {
        try {
            if (imageNo >= 0 && imageNo < 4) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SHUTDOWN_IMAGE, imageNo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }


    public int getSystemShutdownImageNo() {
        int imageno = 0;
        try {
            imageno = Settings.System.getInt(mContext.getContentResolver(),
                    Settings.System.SYS_SHUTDOWN_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageno;
    }

    /*
     *	get shutdown image array
     */
    public int[] getShutdownImageArray() {
        return shutdownImageArray;
    }

    /*
     *	get shutdown image array
     */
    public String[] getShutdownImageArrayEx() {
        return shutdownImageArrayEx;
    }


    /*
     *	set screen off time
     *   @param second -1:never ,
     */
    public void setScreenOffTime(long second) {
        try {
            Settings.System.putLong(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, second == -1 ? -1 : second * 1000);
            if (second != -1) {
                saveTime(second * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTime(long second) {
        SharedPreferences sp = mContext.getSharedPreferences("system", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("screenOffSecond", second);
        editor.apply();
    }

    public void initScreenOffTime() {
        long time = getScreenOffTime();
        if (time <= 0) {
            SharedPreferences sp = mContext.getSharedPreferences("system", Context.MODE_PRIVATE);
            time = sp.getLong("screenOffSecond", 300000);
            setScreenOffTime(time);
        }
    }


    public int getScreenOffTime() {
        long time = -1;
        try {
            time = Settings.System.getLong(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
            if (time != -1) {
                time /= 1000;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    /*
     *	set screen light
     */
    public void setScreenLight(int brightness) {
        setBringhtness(brightness);
    }

    /*
     *	set screen light
     */
    public int gsetScreenLight() {
        return getBringhtness();
    }


    /*
     *	factoryreset
     */
    public void factoryReset() {
        mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
    }


    /*
     *	getSystemInfo
     */
    public Map<String, String> getSystemInfo() {
        Map<String,String> map = new HashMap<>();
        String sn = SystemProperties.get(YITOA_PRODUCT_SN);
        String mode = Build.MODEL;
        String android_ver = Build.VERSION.RELEASE;
        String kernel_version = getFormattedKernelVersion();

        map.put("Serial Number", sn);
        map.put("Mode", mode);
        map.put("Android Version", android_ver);
        map.put("Kernel Version", kernel_version);

        return map;
    }


    public int getBluetoothFlag() {
        int flag = 0;
        String have = SystemProperties.get(HAVE_BLUETOOTH);
        switch (have) {
            case "0":
                flag = 0;
                break;
            case "1":
                flag = 1;
                break;
            case "2":
                flag = 2;
                break;
        }

        return flag;
    }


    public int getFingerprintFlag() {
        int flag = 1;
        try {
            String have = SystemProperties.get(HAVE_FINGERPRINT);
            if (have.equals("0")) {
                flag = 0;
            }
        }catch (Exception e){
            Toast.makeText(mContext, "无法执行相关操作，缺少系统权限！", Toast.LENGTH_SHORT).show();
        }
        return flag;
    }


}
