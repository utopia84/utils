package com.audio.player.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.audio.player.MediaPlayerAdapter;
import com.audio.player.PlaybackInfoListener;
import com.audio.player.PlayerAdapter;
import com.audio.player.data.AudioLibrary;
import com.audio.player.data.LoadPlayData;
import com.audio.player.data.PlayDataLoader;
import com.audio.player.data.PlaySPUtils;
import com.audio.player.util.UICLog;

import java.util.List;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 13:43
 * @描述 播放服务
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

        //log设置
        UICLog.setTAG("test");

        //用于和客户端连接
        mSession = new MediaSessionCompat(getContext(), TAG);
        playDataLoader = new PlayDataLoader();
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        mPlayback = new MediaPlayerAdapter(getContext());
        mPlayback.setPlaybackInfoListener(new MediaPlayerListener());
        UICLog.e("onCreate: AudioService creating MediaSession");

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
        UICLog.e("onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot(AudioLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
        //添加数据
        LoadPlayData.getDefault().goGetData(new LoadPlayData.LoadDataCallback() {
            @Override
            public void completeLoad(LoadPlayData.PlayData data) {
                result.sendResult(data.result);
            }
        });
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
            UICLog.e("MediaSessionCompat.Callback: onPrepare");
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
            UICLog.e("MediaSessionCompat.Callback: onPlay");
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
            UICLog.e("MediaSessionCompat.Callback: onPause");
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
            UICLog.e("MediaSessionCompat.Callback: onStop");
        }

        @Override
        public void onSkipToNext() {
            UICLog.e("MediaSessionCompat.Callback: onSkipToNext");
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
            UICLog.e("MediaSessionCompat.Callback: onSkipToPrevious");
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
            UICLog.e("MediaSessionCompat.Callback: onSeekTo");
            mPlayback.seekTo(pos);
            PlaySPUtils.isPlayEnd = false;
        }
    }

    public class MediaPlayerListener extends PlaybackInfoListener {

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
