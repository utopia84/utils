package com.utopia.logan;

import android.util.Log;

public class LogWriter {

    private static OnLoganProtocolStatus sLoganProtocolStatus;
    private static LoganControlCenter sLoganControlCenter;
    static boolean sDebug = false;

    /**
     * @brief Logan初始化
     */
    public static void init(LoganConfig loganConfig) {
        sLoganControlCenter = LoganControlCenter.instance(loganConfig);
    }

    /**
     * @param log  表示日志内容
     * @param type 表示日志类型
     * @brief Logan写入日志
     */
    public static void writeLog(LogType type, String log) {
        if (sLoganControlCenter == null) {
            Log.e("logan","Please initialize LogWriter first");
        }else{
            sLoganControlCenter.write(type, log + "\n");
        }
        Log.e("logan","正在写入日志："+log);

    }

    public static void writeFunLog(String log) {
        if (sLoganControlCenter == null) {
            Log.e("logan","Please initialize LogWriter first");
        }else{
            Log.e("logan","正在写入日志："+log);
            sLoganControlCenter.write(LogType.FUNCTIONLOG, log + "\n");
        }
    }


    public static void writeBehLog(String log) {
        if (sLoganControlCenter == null) {
            Log.e("logan","Please initialize LogWriter first");
        }else{
            Log.e("logan","正在写入日志："+log);
            sLoganControlCenter.write(LogType.BEHAVIORLOG, log + "\n");
        }
    }

    /**
     * @brief 立即写入日志文件
     */
    public static void f() {
        if (sLoganControlCenter == null) {
            throw new RuntimeException("Please initialize LogWriter first");
        }
        sLoganControlCenter.flush();
    }

    /**
     * @brief 发送日志
     * @param full 是否发送全部内容
     */
    public static void s(boolean full) {
        if (sLoganControlCenter == null) {
            throw new RuntimeException("Please initialize LogWriter first");
        }
        sLoganControlCenter.send(full);
    }


    /**
     * @brief LogWriter Debug开关
     */
    public static void setDebug(boolean debug) {
        LogWriter.sDebug = debug;
    }

    static void onListenerLogWriteStatus(String name, int status) {
        if (sLoganProtocolStatus != null) {
            sLoganProtocolStatus.loganProtocolStatus(name, status);
        }
    }

    public static void setOnLoganProtocolStatus(OnLoganProtocolStatus listener) {
        sLoganProtocolStatus = listener;
    }
}
