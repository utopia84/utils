package com.audio.player.listener;

import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

public class MediaBrowserListener extends MediaControllerCompat.Callback {
    private MediaControllerCallback callback;

    public MediaBrowserListener(MediaControllerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
        callback.onPlayStateChanged(playbackState);
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        if (mediaMetadata == null) {
            return;
        }
        callback.onPlayDataChanged(mediaMetadata);
    }

    @Override
    public void onSessionDestroyed() {
        super.onSessionDestroyed();
    }

    @Override
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        super.onQueueChanged(queue);
    }
}
