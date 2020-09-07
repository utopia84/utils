package eink.yitoa.utils;

import android.app.Application;
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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemUtil {

    private Context mContext;
    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";
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
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns
     * "".
     *
     * @return a string to append to the model number description.
     */
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it,
        // assume it's a
        // production device so that we don't accidentally show that it's an
        // ENGINEERING device.
        try {
            String msv = readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an
            // engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
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
        if (mSystemPresenter.canWriteSettingPermission()) {
            return SystemProperties.get(key);
        }
        return "";
    }

    /**
     * set AirplaneMode
     *
     * @param enable true is airplane open, false is airplane close
     */
    public void setAirplaneMode(boolean enable) {
        Settings.Global.putInt(mContext.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, enable ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enable);
        mContext.sendBroadcast(intent);
    }

    /*
     *
     */
    private void initBringhtness() {
        int level = mSystemPresenter.initBringhtness_level();
        Log.e("test","level1:"+level);
        if (level >= 0 && level < 30) {
        } else {
            level = 0;
        }

        if (mSystemPresenter.canWriteSettingPermission()) {
            try {
                mSystemPresenter.setBringhtness(level);
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_BRIGHTNESS_LEVEL, level);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /*
     *
     */
    public void setBringhtness(int bringhtness_level) {
        try {
            if (bringhtness_level >= 0 && bringhtness_level < 30) {
                if (mSystemPresenter.canWriteSettingPermission()) {
                    mSystemPresenter.setBringhtness(bringhtness_level);
                    Settings.System.putInt(mContext.getContentResolver(),
                            Settings.System.SYS_BRIGHTNESS_LEVEL, bringhtness_level);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
     *
     */
    public int getBringhtness() {
        return mSystemPresenter.initBringhtness_level();
    }

    /*
     *
     */
    public void bringhtnessEnable(boolean enable) {
        mSystemPresenter.bringhtnessEnable(enable);
    }


    /*
     *	reboot device
     */
    public void reboot() {

        Intent BOOXintent = new Intent("onyx.android.show.global.dialog");
        BOOXintent.putExtra("FORCE_REBOOT", true);
        mContext.sendBroadcast(BOOXintent);

        try {
            //mSystemPresenter.reboot();
            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.putExtra("nowait", 1);
            intent.putExtra("interval", 1);
            intent.putExtra("window", 0);
            mContext.sendBroadcast(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "无法执行相关操作，缺少权限！", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *	shutdown device
     */

    public void shutdown() {

        Intent BOOXintent = new Intent("onyx.android.show.global.dialog");
        BOOXintent.putExtra("FORCE_SHUTDOWN", true);
        mContext.sendBroadcast(BOOXintent);

        try {
            Intent intent = new Intent("com.android.internal.intent.action.REQUEST_SHUTDOWN");
            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
            //其中false换成true,会弹出是否关机的确认窗口
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

            mSystemPresenter.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "无法执行相关操作，缺少权限！", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *	set sleep image number
     */
    public void setSystemSleepImage(int imageNo) {
        if (imageNo >= 0) {
            if (mSystemPresenter.canWriteSettingPermission()) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SLEEP_IMAGE, imageNo);
            }

            if ("C68".equals(android.os.Build.MODEL) || "C68".equals(android.os.Build.PRODUCT)) {
                try {
                    String fileName = "standby-" + imageNo + ".png";
                    InputStream in = mContext.getResources().getAssets().open(fileName);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    SleepImageUtils.setSleepImage(mContext, imageBitmap, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     *	set sleep image number
     */
    public void setSystemSleepImage(String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            Matcher m = Pattern.compile("[^0-9]").matcher(fileName);
            String strNo = m.replaceAll("").trim();
            int imageNo = Integer.valueOf(strNo) - 1;
            if (mSystemPresenter.canWriteSettingPermission()) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SLEEP_IMAGE, imageNo);
            }

            if ("C68".equals(android.os.Build.MODEL) || "C68".equals(android.os.Build.PRODUCT)) {
                try {
                    String sleepFileName = "standby-" + imageNo + ".png";
                    InputStream in = mContext.getResources().getAssets().open(sleepFileName);
                    Bitmap imageBitmap = BitmapFactory.decodeStream(in);
                    SleepImageUtils.setSleepImage(mContext, imageBitmap, sleepFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     *	get sleep image number
     */
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


    /*
     *	get sleep image array
     */
    public int[] getSleepImageArray() {
        return sleepImageArray;
    }


    /*
     *	get sleep image array
     */
    public String[] getSleepImageArrayEx() {
        return sleepImageArrayEx;
    }

    /*
     *	set shutdown image number
     */
    public void setSystemShutdownImage(String fileName) {

        if (!TextUtils.isEmpty(fileName)) {
            Matcher m = Pattern.compile("[^0-9]").matcher(fileName);
            String strNo = m.replaceAll("").trim();
            int imageNo = Integer.valueOf(strNo) - 1;
            if (mSystemPresenter.canWriteSettingPermission()) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SHUTDOWN_IMAGE, imageNo);
            }
        }
    }


    /*
     *	set shutdown image number
     */
    public void setSystemShutdownImage(int imageNo) {
        if (imageNo >= 0 && imageNo < 4) {
            if (mSystemPresenter.canWriteSettingPermission()) {
                Settings.System.putInt(mContext.getContentResolver(),
                        Settings.System.SYS_SHUTDOWN_IMAGE, imageNo);
            }
        }
    }

    /*
     *	get shutdown image number
     */
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
            if (mSystemPresenter.canWriteSettingPermission()) {
                Settings.System.putLong(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, second == -1 ? -1 : second * 1000);
                if (second != -1){
                    saveTime(second * 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTime(long second){
        Log.e("test","second:"+second);
        SharedPreferences sp = mContext.getSharedPreferences("system", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("screenOffSecond",second);
        editor.apply();
    }

    public void initScreenOffTime(){
        long time = getScreenOffTime();
        if (time <= 0) {
            SharedPreferences sp = mContext.getSharedPreferences("system", Context.MODE_PRIVATE);
            time = sp.getLong("screenOffSecond", 300000);
            setScreenOffTime(time);
        }
    }
    /*
     *	set screen off time
     */
    public int getScreenOffTime() {
        long time;

        time = Settings.System.getLong(mContext.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1);
        if (time != -1)
            time /= 1000;

        Log.e("test","time:"+time);
        return (int) time;
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
        Map map = new HashMap();
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

    /*
     *	getBluetoothFlag
     * return 0:û������������¼�� PCBV2.2, 1:������������¼��PCBV2.2, 2:��������������¼�� PCBV2.1
     */
    public int getBluetoothFlag() {
        int flag = 0;
        String have = SystemProperties.get(HAVE_BLUETOOTH);
        if (have.equals("0"))
            flag = 0;
        else if (have.equals("1"))
            flag = 1;
        else if (have.equals("2"))
            flag = 2;

        return flag;
    }

    /*
     *	getFingerprintFlag
     * return 0:no, 1:yes
     */
    public int getFingerprintFlag() {
        int flag = 1;
        String have = SystemProperties.get(HAVE_FINGERPRINT);
        if (have.equals("0"))
            flag = 0;

        return flag;
    }


}
