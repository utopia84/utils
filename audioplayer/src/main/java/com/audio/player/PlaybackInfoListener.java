package com.audio.player;

import android.support.v4.media.session.PlaybackStateCompat;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 15:08
 * @描述 状态更新监听
 */
public abstract class PlaybackInfoListener {

    public abstract void onPlaybackStateChange(PlaybackStateCompat state);

    public void onPlaybackCompleted() {
    }

}
