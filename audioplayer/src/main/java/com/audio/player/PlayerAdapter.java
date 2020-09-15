package com.audio.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.audio.player.util.TimeCountUtil;

import androidx.annotation.NonNull;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 14:52
 * @描述 控制app音频焦点监听事件和耳机插拔事件
 */
public abstract class PlayerAdapter {

    private Context mApplicationContext;
    private AudioManager mAudioManager;//音频管理器
    private AudioFocusHelper mAudioFocusHelper;

    private boolean audioNoisyReceiverRegistered = false;

    //监听耳机插拔广播
    private BroadcastReceiver mAudioNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                if (isPlaying()) {
                    pause();
                }
            }
        }
    };

    public PlayerAdapter(@NonNull Context context){
        mApplicationContext = context.getApplicationContext();
        mAudioManager = (AudioManager) mApplicationContext.getSystemService(Context.AUDIO_SERVICE);
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

    private void registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(mAudioNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
            audioNoisyReceiverRegistered = true;
        }
    }

    private void unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver);
            audioNoisyReceiverRegistered = false;
        }
    }

    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener{

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
