package com.audio.player.data.db;

import org.litepal.crud.LitePalSupport;

/**
 * @作者 邸昌顺
 * @时间 2019/3/20 16:30
 * @描述
 */
public class AudioBookProgress extends LitePalSupport {

    private String bookId;
    private String curChapterId;
    private String curChapterName;
    private long position;
    private long duration;
    private boolean isSync;//true需要同步；
    private String userId;
    private String audioId;
    private long updateTime;

    public AudioBookProgress() {
        this.updateTime = System.currentTimeMillis();
    }

    public AudioBookProgress(String bookId, String curChapterId, String curChapterName, long position, long duration, boolean isSync, String userId, String audioId) {
        this.bookId = bookId;
        this.curChapterId = curChapterId;
        this.curChapterName = curChapterName;
        this.position = position;
        this.duration = duration;
        this.isSync = isSync;
        this.userId = userId;
        this.audioId = audioId;
        this.updateTime = System.currentTimeMillis();
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getCurChapterId() {
        return curChapterId;
    }

    public void setCurChapterId(String curChapterId) {
        this.curChapterId = curChapterId;
    }

    public String getCurChapterName() {
        return curChapterName;
    }

    public void setCurChapterName(String curChapterName) {
        this.curChapterName = curChapterName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}
