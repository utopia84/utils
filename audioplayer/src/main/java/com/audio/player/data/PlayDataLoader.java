package com.audio.player.data;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者 邸昌顺
 * @时间 2019/7/2 10:34
 * @描述 处理数据的封装
 */
public final class PlayDataLoader {

    private List<MediaSessionCompat.QueueItem> mediaQueue; //要播放的资源的队列
    private int queueIndex = -1; //当前播放的资源在队列中的索引
    private MediaMetadataCompat playMediaMetadata;
    private MediaDescriptionCompat playMediaDescription;

    public PlayDataLoader(){
        mediaQueue = new ArrayList<>();
    }

    public void addQueueItem(MediaDescriptionCompat description){
        mediaQueue.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
        queueIndex = (queueIndex == -1) ? 0 : queueIndex; //初始队列索引，添加资源之后就变成0
    }

    public void removeQueueItem(MediaDescriptionCompat description){
        mediaQueue.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
    }

    public int getQueueIndex(){
        return queueIndex;
    }

    public List<MediaSessionCompat.QueueItem> getMediaQueue(){
        return mediaQueue;
    }

    public boolean notToPrepare(){
        return queueIndex < 0 && mediaQueue.isEmpty();
    }

    public void prepareMedia(){
        PlaySPUtils.isPlayEnd = false;
        //控制播放的item
        String mediaId;
        if(TextUtils.isEmpty(PlaySPUtils.getCurrentSongId())){//初始
            queueIndex = 0;
            mediaId = mediaQueue.get(queueIndex).getDescription().getMediaId();

            PlaySPUtils.savePosition(queueIndex);
            PlaySPUtils.saveCurrentSongId(mediaId);
            PlaySPUtils.setPlayPosition(0);
        }else{//已有播放记录
            queueIndex = PlaySPUtils.getPosition();
            mediaId = PlaySPUtils.getCurrentSongId();
        }
        playMediaDescription = mediaQueue.get(queueIndex).getDescription();
        playMediaMetadata = LoadPlayData.getMediaMetadata(playMediaDescription);
    }

    public boolean notReadyToPlay(){
        return mediaQueue.isEmpty();
    }

    public MediaMetadataCompat getPlayMediaMetadata(){
        return playMediaMetadata;
    }

    public MediaDescriptionCompat getPlayMediaDescription(){
        return playMediaDescription;
    }

    public boolean skipToNextMedia(){

        queueIndex++;
        if(queueIndex > mediaQueue.size()-1){
            queueIndex = mediaQueue.size()-1;
            PlaySPUtils.isPlayEnd = true;//自动播放停止
            return false;
        }else{
            String mediaId = mediaQueue.get(queueIndex).getDescription().getMediaId();

            //保存播放信息
            PlaySPUtils.setPlayPosition(0);
            PlaySPUtils.savePosition(queueIndex);
            PlaySPUtils.saveCurrentSongId(mediaId);
            PlaySPUtils.isPlayEnd = false;

            playMediaDescription = mediaQueue.get(queueIndex).getDescription();
            playMediaMetadata = LoadPlayData.getMediaMetadata(playMediaDescription);
            return true;
        }
    }

    public boolean skipToLastMedia(){
        queueIndex--;
        if(queueIndex < 0){
            queueIndex = 0;
            return false;
        }else {
            String mediaId = mediaQueue.get(queueIndex).getDescription().getMediaId();

            //保存播放信息
            PlaySPUtils.setPlayPosition(0);
            PlaySPUtils.savePosition(queueIndex);
            PlaySPUtils.saveCurrentSongId(mediaId);
            PlaySPUtils.isPlayEnd = false;

            playMediaDescription = mediaQueue.get(queueIndex).getDescription();
            playMediaMetadata = LoadPlayData.getMediaMetadata(playMediaDescription);
            return true;
        }
    }

}
