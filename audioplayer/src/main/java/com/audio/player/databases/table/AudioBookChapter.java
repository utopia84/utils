package com.audio.player.databases.table;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_audio_book_chapter")
public class AudioBookChapter {
    @PrimaryKey
    @NonNull
    private String chapterId;
    private String bookId;
    private String uri;
    private String name;
    private long duration;
    private int orderNo;

    public AudioBookChapter() {
    }

    @Ignore
    public AudioBookChapter(String chapterId, String bookId, String uri, String name, long duration, int orderNo) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.uri = uri;
        this.name = name;
        this.duration = duration;
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
}
