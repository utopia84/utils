package com.utopia.logan.gzip;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReadDetailBean {
    public String tab;
    public String type;
    public String user_id;
    public String book_id;
    public String page_num;
    public String page_words;
    public String begin_time;
    public String end_time;
    public String device_num;
    public String total_words;
    public String read_words;
    public String source;
    public String page_type;
    public String eink_version;

    public ReadDetailBean() {
    }


    public String getDate(){
        String date = null;
        if (begin_time != null && begin_time.length() > 10){
            date = begin_time.substring(0,10);
        }

        if (TextUtils.isEmpty(date)){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);//设置日期格式
            date = df.format(new Date());//为获取当前系统日期
        }

        return date;
    }

    public long getReadSeconds(){//获取阅读秒数
        long sec = 0 ;
        if (!TextUtils.isEmpty(begin_time) && !TextUtils.isEmpty(end_time)){
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
                long beginDate = format.parse(begin_time).getTime();
                long endDate = format.parse(end_time).getTime();
                if (endDate > beginDate) {
                    sec = (endDate - beginDate) / 1000;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return sec;
    }

    @Override
    public String toString() {
        return "ReadDetailBean{" +
                "tab='" + tab + '\'' +
                ", type='" + type + '\'' +
                ", user_id='" + user_id + '\'' +
                ", book_id='" + book_id + '\'' +
                ", page_num='" + page_num + '\'' +
                ", page_words='" + page_words + '\'' +
                ", begin_time='" + begin_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", device_num='" + device_num + '\'' +
                ", total_words='" + total_words + '\'' +
                ", read_words='" + read_words + '\'' +
                ", source='" + source + '\'' +
                ", page_type='" + page_type + '\'' +
                ", eink_version='" + eink_version + '\'' +
                '}';
    }
}
