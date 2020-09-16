package com.audio.player.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.audio.player.MediaPlayerAdapter;
import com.audio.player.listener.PlaybackInfoListener;
import com.audio.player.data.AudioLibrary;
import com.audio.player.data.LoadPlayData;
import com.audio.player.data.PlayDataLoader;
import com.audio.player.listener.Callback;
import com.audio.player.util.PlaySPUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

/**
 * 播放服务
 */
public class AudioService extends MediaBrowserServiceCompat {

    private static final String TAG = AudioService.class.getSimpleName();

    private MediaSessionCompat mSession;
    private MediaSessionCallback mCallback;
    private MediaPlayerAdapter mPlayback;
    private PlayDataLoader playDataLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        //用于和客户端连接
        mSession = new MediaSessionCompat(getContext(), TAG);
        playDataLoader = new PlayDataLoader();
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        mPlayback = new MediaPlayerAdapter();
        mPlayback.setPlaybackInfoListener(new MediaPlayerListener());

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        mPlayback.stop();
        mSession.release();
    }

    @NonNull
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, Bundle rootHints) {
        return new BrowserRoot(AudioLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        //添加数据
        LoadPlayData.getDefault().getPlayData((Callback<LoadPlayData.PlayData>) data -> result.sendResult(data.result));
    }

    private Context getContext(){
        return AudioService.this;
    }

    /**
     * 客户端控制播放操作，这是操作的回调MediaSessionCompat.Callback；
     *
     * */
    public class MediaSessionCallback extends MediaSessionCompat.Callback{

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            playDataLoader.addQueueItem(description);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            playDataLoader.removeQueueItem(description);
            mSession.setQueue(playDataLoader.getMediaQueue());
        }

        @Override
        public void onPrepare() {
            mSession.setQueue(playDataLoader.getMediaQueue());

            if(playDataLoader.notToPrepare()){
                return;
            }
            playDataLoader.prepareMedia();
            mSession.setMetadata(playDataLoader.getPlayMediaMetadata());
            if(!mSession.isActive()){
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {
            if(playDataLoader.notReadyToPlay()){
                return;
            }

            if(playDataLoader.getPlayMediaMetadata() == null){
                Log.e("test","onPlay:3");
                onPrepare();
            }

            if (mPlayback !=null) {
                mPlayback.playFromUriByPosition(playDataLoader.getPlayMediaMetadata());
            }
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            if(playDataLoader.skipToNextMedia()){
                mSession.setMetadata(playDataLoader.getPlayMediaMetadata());
                if(!mSession.isActive()){
                    mSession.setActive(true);
                }
                mPlayback.playFromUriByPosition(playDataLoader.getPlayMediaMetadata());
            }else {
                onStop();
            }
        }

        @Override
        public void onSkipToPrevious() {
            if(playDataLoader.skipToLastMedia()){
                mSession.setMetadata(playDataLoader.getPlayMediaMetadata());
                if(!mSession.isActive()){
                    mSession.setActive(true);
                }
                mPlayback.playFromUriByPosition(playDataLoader.getPlayMediaMetadata());
            }
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
            PlaySPUtils.isPlayEnd = false;
        }
    }

    public class MediaPlayerListener implements PlaybackInfoListener {

        MediaPlayerListener() {}

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            mSession.setPlaybackState(state);
        }

        @Override
        public void onPlaybackCompleted() {
            mCallback.onSkipToNext();
        }

    }
}
