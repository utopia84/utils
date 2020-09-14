package eink.yitoa.utils;

import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;

public class SystemPresenter {
    private Context mContext;
    private PowerManager mPowerManager;
    private int mMinimumScreenBrightness;
    private int mMaximumScreenBrightness;
    private int mBaseScreenBrightness;
    private int mstepScreenBrightness;

    public SystemPresenter(Context context) {
        mContext = context.getApplicationContext();
        mPowerManager = (PowerManager) mContext
                .getSystemService(Context.POWER_SERVICE);
        if (mPowerManager != null) {
            mMinimumScreenBrightness = mPowerManager.getMinimumScreenBrightnessSetting();
            mMaximumScreenBrightness = mPowerManager.getMaximumScreenBrightnessSetting();
        }

        mMinimumScreenBrightness = 45;//90;
        int mMaxStepScreenBrightness = 30;//15;

        mstepScreenBrightness = (mMaximumScreenBrightness - mMinimumScreenBrightness) / mMaxStepScreenBrightness;
        mBaseScreenBrightness = mMinimumScreenBrightness - mstepScreenBrightness;
    }

    public int initBringhtness_level() {
        int brightness = -1;
        try {
            brightness = Settings.System.getInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (brightness > mBaseScreenBrightness
                && brightness <= mMaximumScreenBrightness) {
            brightness = (brightness - mBaseScreenBrightness) / mstepScreenBrightness;
        } else {
            brightness = 0;
        }
        return brightness;
    }

    public void setBringhtness(int bringhtness_level) {
        int brightness;

        if (bringhtness_level == 0) {
            brightness = 15;//mMinimumScreenBrightness;
        } else {
            if(mBaseScreenBrightness == 0)
                brightness = bringhtness_level * mstepScreenBrightness + mstepScreenBrightness-1;
            else
                brightness = bringhtness_level * mstepScreenBrightness + mBaseScreenBrightness;

            if(brightness >=  mMaximumScreenBrightness) {
                brightness = mMaximumScreenBrightness;
            }
        }

        try {
            Settings.System.putLong(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, brightness);
        }catch(Exception e) {
            e.printStackTrace();
        }

        //mPowerManager.setBacklightBrightness(brightness);

        if (brightness < mMinimumScreenBrightness) {
            //mPowerManager.disableLight();
        }
    }


    public void reboot() throws SecurityException{
        mPowerManager.reboot(null);
    }

    public void shutdown() throws SecurityException{
        mPowerManager.shutdown(false);
    }
}
