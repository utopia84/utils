package com.audio.player.util;


import java.util.Timer;
import java.util.TimerTask;

/**
 * @作者 邸昌顺
 * @时间 2019/3/20 13:23
 * @描述
 */
public final class TimeCountUtil {

    public TimeCountUtil(){

    }

    private Timer timer;
    private TimerTask timerTask;

    public void start(final Runnable runnable){
        if(timer == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runnable.run();
                }
            };
            timer.schedule(timerTask, 0L, 1000L);
        }
    }

    public void cancel(){
        if(timerTask != null){
            timerTask.cancel();
            timer.cancel();
            timerTask = null;
            timer = null;
        }
    }

}
