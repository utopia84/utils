package com.utopia.logan.gzip;

public class StatisticsDayEntity {
    public String primaryKey = "";
    public String tab = "statistics_day";
    public String userId;
    public String bookId;
    public String date;
    public int num = 0;
    public long times;
    public String pageNum;
    public int totalWords;
    public int readWords;
    public String beginTime;


    public StatisticsDayEntity(String user_id, String book_id, int total_words, String date) {
        this.userId = user_id;
        this.bookId = book_id;
        this.date = date;
        this.totalWords = total_words;
        this.times = 0;
        this.num = 0 ;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setPage_num(String page_num) {
        this.pageNum = page_num;
    }

    public void setRead_words(int read_words) {
        this.readWords = read_words;
    }

    public void setBegin_time(String begin_time) {
        this.beginTime = begin_time;
    }

    public void addReadWordsNum(int count) {
        if (count > 0) {
            this.num += count;
        }
    }

    public void addTimes(long sec , boolean useRules){
        if (useRules && sec > 900){
            sec = 900;
        }
        times += sec;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String toJsonString() {
        return "{" +
                "tab='" + tab + '\'' +
                ", userId='" + userId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", date='" + date + '\'' +
                ", num=" + num +
                ", times=" + times +
                ", pageNum='" + pageNum + '\'' +
                ", totalWords=" + totalWords +
                ", readWords=" + readWords +
                ", beginTime='" + beginTime + '\'' +
                '}';
    }
}
