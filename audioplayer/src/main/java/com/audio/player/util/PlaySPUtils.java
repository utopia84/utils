package com.audio.player.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

public class PlaySPUtils {

    @SuppressLint("StaticFieldLeak")
    private static Context mApplicationContext;

    public static void instance(Context context){
        mApplicationContext = context;
    }

    public static boolean isPlayEnd = false;
    public static boolean isPauseByUser = false; //控制用户暂停动作

    private static final String AUDIO_ID = "audio_id";
    private static final String PLAY_POSITION = "play_position";
    private static final String QUEUE_POSITION = "queue_position";

    public static long getPlayPosition() {
        return getAnyByKey(PLAY_POSITION, 0L);
    }

    public static void setPlayPosition(long position) {
        putAnyCommit(PLAY_POSITION, position);
    }

    public static String getCurrentSongId() {
        return getAnyByKey(AUDIO_ID, "");
    }

    public static void saveCurrentSongId(String mid) {
        putAnyCommit(AUDIO_ID, mid);
    }

    public static int getPosition() {
        return getAnyByKey(QUEUE_POSITION, 0);
    }

    public static void savePosition(int id) {
        putAnyCommit(QUEUE_POSITION, id);
    }

    /**
     * -------------------------------------------------------
     * <p>底层操作
     * -------------------------------------------------------
     */
    public static boolean getAnyByKey(String key, boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static void putAnyCommit(String key, boolean value) {
        getPreferences().edit().putBoolean(key, value).apply();
    }

    public static float getAnyByKey(String key, float defValue) {
        return getPreferences().getFloat(key, defValue);
    }

    public static void putAnyCommit(String key, float value) {
        getPreferences().edit().putFloat(key, value).apply();
    }

    public static int getAnyByKey(String key, int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static void putAnyCommit(String key, int value) {
        getPreferences().edit().putInt(key, value).apply();
    }

    public static long getAnyByKey(String key, long defValue) {
        return getPreferences().getLong(key, defValue);
    }

    public static void putAnyCommit(String key, long value) {
        getPreferences().edit().putLong(key, value).apply();
    }

    public static String getAnyByKey(String key, @Nullable String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static void putAnyCommit(String key, @Nullable String value) {
        getPreferences().edit().putString(key, value).apply();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
    }
}
