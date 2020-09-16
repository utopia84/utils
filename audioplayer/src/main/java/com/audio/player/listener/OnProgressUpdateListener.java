package com.audio.player.listener;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 17:32
 * @描述
 */
public interface OnProgressUpdateListener {

    void onProgressUpdate(long progress);

    void setMax(long max);

}
