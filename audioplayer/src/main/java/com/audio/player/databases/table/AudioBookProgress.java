package com.audio.player.databases.table;

import com.audio.player.databases.DBHelper;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = DBHelper.TABLE_AUDIO_PROGRESS,primaryKeys = {"userId","chapterId"})
public class AudioBookProgress{
    @NonNull private String userId;//用户id
    @NonNull private String chapterId;//章节id
    private String audioId;//章节音频id

    private long position;//章节当前播放时长
    private boolean isSync;//true需要同步；
    private long updateTime;//更新时间

    public AudioBookProgress() {

    }

    @Ignore
    public AudioBookProgress(String userId, String chapterId, long position,  boolean isSync,  String audioId) {
        this.chapterId = chapterId;
        this.userId = userId;
        this.position = position;
        this.isSync = isSync;
        this.audioId = audioId;
        this.updateTime = System.currentTimeMillis();
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(@NonNull String chapterId) {
        this.chapterId = chapterId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
