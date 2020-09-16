package com.audio.player.listener;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

public interface MediaControllerCallback {
    void onPlayStateChanged(PlaybackStateCompat playbackState);
    void onPlayDataChanged(MediaMetadataCompat mediaMetadata);
}
