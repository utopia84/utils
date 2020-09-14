package com.audio.player.data.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * @作者 邸昌顺
 * @时间 2019/3/20 17:38
 * @描述
 */
public class AudioBookChapter extends LitePalSupport {

    @Column(unique = true, defaultValue = "unknown")
    private String chapterId;

    private String bookId;
    private String uri;
    private String name;
    @Column(defaultValue = "0")
    private long duration;
    private int orderNo;

    public AudioBookChapter() {
    }

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
