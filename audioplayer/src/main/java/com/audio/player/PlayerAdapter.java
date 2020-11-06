package com.audio.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.MediaMetadataCompat;

import com.audio.player.util.ApplicationUtils;

/**
 * 控制app音频焦点监听事件和耳机插拔事件
 */
abstract class PlayerAdapter {
    private AudioFocusHelper mAudioFocusHelper;

    private boolean audioNoisyReceiverRegistered = false;

    //监听耳机插拔广播
    private BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (isPlaying()) {//耳机状态改变时，暂停当前播放状态
                    pause();
                }
            }
        }
    };

    public PlayerAdapter(){
        mAudioFocusHelper = new AudioFocusHelper();
    }

    public abstract void playFromUriByPosition(MediaMetadataCompat metadata);

    public abstract boolean isPlaying();

    public final void play() {
        if (mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver();
            onPlay();
        }
    }

    protected abstract void onPlay();

    public final void pause() {
        unregisterAudioNoisyReceiver();
        onPause();
    }

    protected abstract void onPause();

    public final void stop() {
        mAudioFocusHelper.abandonAudioFocus();
        unregisterAudioNoisyReceiver();
        onStop();
    }

    protected abstract void onStop();

    public abstract void seekTo(long position);

    /**
     * 注册耳机监听
     */
    private void registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegistered) {
            ApplicationUtils.getApplication().registerReceiver(mAudioNoisyReceiver,
                    new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
            audioNoisyReceiverRegistered = true;
        }
    }

    /**
     * 取消耳机监听
     */
    private void unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegistered) {
            ApplicationUtils.getApplication().unregisterReceiver(mAudioNoisyReceiver);
            audioNoisyReceiverRegistered = false;
        }
    }

    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener{
        private AudioManager mAudioManager;//音频管理器
        AudioFocusHelper(){
            mAudioManager = (AudioManager) ApplicationUtils.getApplication().getSystemService(Context.AUDIO_SERVICE);
        }

        private boolean requestAudioFocus() {
            int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }

        private void abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this);
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    if (!isPlaying()) {
                        play();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (isPlaying()) {
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mAudioManager.abandonAudioFocus(this);
                    stop();
                    break;
            }
        }
    }

}
