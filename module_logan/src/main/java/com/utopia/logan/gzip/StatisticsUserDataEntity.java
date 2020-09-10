package com.utopia.logan.gzip;

public class StatisticsUserDataEntity {
    public String primaryKey = "";
    public String tab = "statistics_user_date";
    public String userId;
    public String date;
    public int num = 0;
    public long times;
    public String beginTime;


    public StatisticsUserDataEntity(String user_id, String date) {
        this.userId = user_id;
        this.date = date;
        this.times = 0;
        this.num = 0 ;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
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

    public String toJsonString() {
        return "{" +
                "tab='" + tab + '\'' +
                ", userId='" + userId + '\'' +
                ", date='" + date + '\'' +
                ", num=" + num +
                ", times=" + times +
                ", beginTime='" + beginTime + '\'' +
                '}';
    }
}
