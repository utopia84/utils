package com.audio.player.util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器工具类
 */
public final class TimeCountUtil {
    private final static long PERIOD = 1000L;

    private Timer timer;
    private TimerTask timerTask;

    /**
     * 开启计时器
     * @param runnable 执行线程
     */
    public void start(final Runnable runnable){
        if(timer == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            };
            timer.schedule(timerTask, 0L, PERIOD);
        }
    }

    /**
     * 结束计时器，释放资源
     */
    public void cancel(){
        if(timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }

        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

}
