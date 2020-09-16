package com.audio.player.listener;

import android.support.v4.media.session.PlaybackStateCompat;

/**
 * 状态更新监听
 */
public interface PlaybackInfoListener {

    void onPlaybackStateChange(PlaybackStateCompat state);

    default void onPlaybackCompleted() {

    }

}
