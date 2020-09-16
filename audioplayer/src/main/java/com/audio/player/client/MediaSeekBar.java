package com.audio.player.client;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.audio.player.data.AudioLibrary;
import com.audio.player.listener.Callback;
import com.audio.player.listener.OnProgressUpdateListener;
import com.audio.player.util.PlaySPUtils;
import com.audio.player.listener.SeekBarEventBus;

public class MediaSeekBar extends SeekBar implements SeekBar.OnSeekBarChangeListener {
    private AudioLibrary audioLibrary;
    private MediaControllerCompat mMediaController;
    private ControllerCallback mControllerCallback;

    private OnProgressUpdateListener onProgressUpdateListener;
    public void setOnProgressUpdateListener(OnProgressUpdateListener onProgressUpdateListener){
        this.onProgressUpdateListener = onProgressUpdateListener;
    }

    private boolean mIsTracking = false;


    public MediaSeekBar(Context context) {
        this(context,null);
    }

    public MediaSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MediaSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setOnSeekBarChangeListener(this);
        audioLibrary = new AudioLibrary();
    }

    @Override
    public final void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        throw new UnsupportedOperationException("Cannot add listeners to a MediaSeekBar");
    }

    public void setMediaController(final MediaControllerCompat mediaController) {
        if (mediaController != null) {
            mControllerCallback = new ControllerCallback();
            mediaController.registerCallback(mControllerCallback);
        } else if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
        }
        mMediaController = mediaController;
    }

    public void disconnectController() {
        if (mMediaController != null) {
            mMediaController.unregisterCallback(mControllerCallback);
            mControllerCallback = null;
            mMediaController = null;
        }
    }

    public void releaseTimeCount(){
        SeekBarEventBus.removeObject("seekBar");
    }

    public void notifyProgress(long position){
        if (mIsTracking) {
            return;
        }
        if(mMediaController != null){
            if(position > getMax()){
                position = getMax();
            }
            PlaySPUtils.setPlayPosition(position);
            setProgress((int) position);
            onProgressUpdateListener.onProgressUpdate(position);//注意空指针
        }
    }

    private class ControllerCallback extends MediaControllerCompat.Callback{

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onPlaybackStateChanged(final PlaybackStateCompat state) {
            releaseTimeCount();
            if(PlaySPUtils.isPlayEnd || (state != null && state.getState() == PlaybackStateCompat.STATE_STOPPED)){
                if(mMediaController != null){
                    long position = mMediaController.getPlaybackState().getPosition();
                    if(position != 0){
                        return;
                    }
                    if(position > getMax()){
                        position = getMax();
                    }
                    PlaySPUtils.setPlayPosition(position);
                    if(onProgressUpdateListener != null){
                        onProgressUpdateListener.onProgressUpdate(position);//注意空指针
                    }
                }
                return;
            }
            if (state != null && state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                SeekBarEventBus.setObject(MediaSeekBar.this,"seekBar");
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if(metadata == null){
                return;
            }
            audioLibrary.getAudioDuration(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID), new Callback<Long>() {
                @Override
                public void finished(Long max) {
                    setMax(max.intValue());
                    long progress = PlaySPUtils.getPlayPosition();
                    setProgress((int) progress);
                    if(onProgressUpdateListener != null){
                        onProgressUpdateListener.onProgressUpdate(progress);
                        onProgressUpdateListener.setMax(max);
                    }
                }
            });
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsTracking = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        PlaySPUtils.isPlayEnd = seekBar.getProgress() >= seekBar.getMax();
        PlaySPUtils.setPlayPosition(seekBar.getProgress());
        if(onProgressUpdateListener != null){
            onProgressUpdateListener.onProgressUpdate(seekBar.getProgress());//注意空指针
        }
        mMediaController.getTransportControls().seekTo(getProgress());//重新调整了播放进度
        mIsTracking = false;
    }
}
