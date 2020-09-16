package com.audio.player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import com.audio.player.client.MediaBrowserHelper;
import com.audio.player.client.MediaSeekBar;
import com.audio.player.service.AudioService;

import java.util.List;

import androidx.annotation.NonNull;


public class MediaBrowserConnection extends MediaBrowserHelper {
    private MediaSeekBar mMediaSeekBar;

    public MediaBrowserConnection(Context context , MediaSeekBar mediaSeekBar) {
        super(context, AudioService.class);
        this.mMediaSeekBar = mediaSeekBar;
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController) {
        mMediaSeekBar.setMediaController(mediaController);
    }

    @Override
    protected void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);
        getMediaController().getTransportControls().prepare();
        final MediaControllerCompat mediaController = getMediaController();
        for (final MediaBrowserCompat.MediaItem mediaItem : children) {
            mediaController.addQueueItem(mediaItem.getDescription());
        }
        mediaController.getTransportControls().prepare();
    }
}
