package com.audio.player.databases.table;

import com.audio.player.databases.DBHelper;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = DBHelper.TABLE_AUDIO_CHAPTER)
public class AudioBookChapter {
    @PrimaryKey
    @NonNull
    private String chapterId;   //章节id
    private String bookId;//图书id
    private String uri;//音频URL
    private String name;//章节名称
    private int orderNo;//章节排序
    private long duration;//章节总时长

    public AudioBookChapter() {

    }

    @Ignore
    public AudioBookChapter(String chapterId, String bookId, String uri, String name, int orderNo) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.uri = uri;
        this.name = name;
        this.orderNo = orderNo;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
