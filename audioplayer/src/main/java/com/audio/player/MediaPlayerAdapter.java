package com.audio.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.audio.player.client.MediaSeekBar;
import com.audio.player.data.AudioLibrary;
import com.audio.player.data.LoadPlayData;
import com.audio.player.data.PlaySPUtils;
import com.audio.player.listener.SeekBarEventBus;
import com.audio.player.util.TimeCountUtil;
import com.audio.player.util.UICLog;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 15:07
 * @描述 音频播放器封装
 */
public class MediaPlayerAdapter extends PlayerAdapter {

    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private PlaybackInfoListener mPlaybackInfoListener;
    private int mState;

    public MediaPlayerAdapter(Context context) {
        super(context);
        mContext = context.getApplicationContext();
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        this.mPlaybackInfoListener = listener;
    }

    @Override
    public void onStop() {
        setNewState(PlaybackStateCompat.STATE_STOPPED);
        release();
    }

    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (timeCount != null) {
            timeCount.cancel();
            timeCount = null;
        }
    }

    private int errorTimes = 0 ;
    @Override
    public void playFromUriByPosition(MediaMetadataCompat metadata) {
        String path = LoadPlayData.getChapterMediaPath(metadata);
        if(TextUtils.isEmpty(path)){
            UICLog.e("Media Path Is Null, Please Check!");
            return;
        }

        if(mMediaPlayer == null){
            Log.e("test","1");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener((MediaPlayer mediaPlayer) -> {
                if(!PlaySPUtils.isPlayEnd && !PlaySPUtils.isPauseByUser){
                    mPlaybackInfoListener.onPlaybackCompleted();
                    setNewState(PlaybackStateCompat.STATE_PLAYING);
                }
            });
            mMediaPlayer.setOnErrorListener((MediaPlayer mp, int what, int extra) -> {
                Log.e("test","error:"+what);
                if (mMediaPlayer != null && errorTimes==0) {
                    errorTimes = 1;
                    mMediaPlayer.pause();
                    PlaySPUtils.isPauseByUser = true;
                    setNewState(PlaybackStateCompat.STATE_PAUSED);
                }else{
                    errorTimes = 0 ;
                }
                return true;//拦截错误
            });
        }else{
            Log.e("test","2");
            mMediaPlayer.reset();
        }

        String mediaId = LoadPlayData.getChapterId(metadata);
        try {
            Log.e("test","path:"+path);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener((MediaPlayer mp) -> {

                AudioLibrary.notifyAudioDuration(mediaId, mMediaPlayer.getDuration(), new AudioLibrary.Callback() {
                    @Override
                    public <T> void next(T t) {
                        Log.e("test","setOnPreparedListener");
                        if(PlaySPUtils.getPlayPosition() >= 0){
                            mMediaPlayer.seekTo((int) PlaySPUtils.getPlayPosition());
                        }
                        play();

                        timeCount = new TimeCountUtil();
                        timeCount.start(()->notifyProgress());
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext,"音频加载失败",Toast.LENGTH_SHORT).show();
        }

        UICLog.e("end");
    }

    TimeCountUtil timeCount;

    private MediaSeekBar mediaSeekBar;
    private void notifyProgress() {
        mediaSeekBar = SeekBarEventBus.getObject("seekBar");
        if (mediaSeekBar != null && mMediaPlayer != null){
            mediaSeekBar.notifyProgress(mMediaPlayer.getCurrentPosition());
        }
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    protected void onPlay() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            PlaySPUtils.isPauseByUser = false;
            setNewState(PlaybackStateCompat.STATE_PLAYING);
        }
    }

    @Override
    protected void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            PlaySPUtils.isPauseByUser = true;
            setNewState(PlaybackStateCompat.STATE_PAUSED);
        }
    }

    private void setNewState(@PlaybackStateCompat.State int newPlayerState) {
        mState = newPlayerState;
        final long reportPosition = mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
        final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
        stateBuilder.setActions(getAvailableActions());
        stateBuilder.setState(mState,
                reportPosition,
                1.0f,
                SystemClock.elapsedRealtime());
        if(mState == PlaybackStateCompat.STATE_PLAYING){
            Bundle extras = new Bundle();
            extras.putLong("duration_update", mMediaPlayer.getDuration());
            stateBuilder.setExtras(extras);
        }
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build());
    }

    @PlaybackStateCompat.Actions
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
        switch (mState) {
            case PlaybackStateCompat.STATE_STOPPED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PAUSE;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                actions |= PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE
                        | PlaybackStateCompat.ACTION_SEEK_TO;
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_STOP;
                break;
            default:
                actions |= PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_STOP
                        | PlaybackStateCompat.ACTION_PAUSE;
        }
        return actions;
    }

    @Override
    public void seekTo(long position) {
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.seekTo((int) position);
                setNewState(mState);
            }
        }
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
