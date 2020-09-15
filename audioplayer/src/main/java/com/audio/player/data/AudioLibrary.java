package com.audio.player.data;

import android.media.MediaPlayer;
import android.util.Log;

import com.audio.player.databases.DatabaseHolder;
import com.audio.player.databases.table.AudioBookChapter;
import com.audio.player.model.BaseSubscriber;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AudioLibrary {

    public interface Callback{
       <T> void next(T t);
    }

    public static long curDuration;

    public static String getRoot(){
        return "root_eink";//随便定义
    }

    public static void notifyAudioDuration(String chapterId, long duration, Callback callback){
        Observable.create((Subscriber<? super AudioBookChapter> subscriber) -> {
                    AudioBookChapter chapter = DatabaseHolder.getInstance().chapterDao().findFirst(chapterId);
                    if(chapter != null){
                        Log.e("test", "duration=" + chapter.getDuration() + ", " + duration);
                        chapter.setDuration(duration);
                        DatabaseHolder.getInstance().chapterDao().update(chapter);
                        curDuration = chapter.getDuration();
                        subscriber.onNext(chapter);
                    }
                })
                .observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<AudioBookChapter>() {
                    @Override
                    public void onNext(AudioBookChapter re) {
                        callback.next(re);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e("test", "onError-" + e.toString());
                    }
                });
    }

    //异步保存时长
    public static void getAudioDuration(String chapterId, Callback callback){
        Observable.create((Subscriber<? super AudioBookChapter> subscriber) -> {

                AudioBookChapter chapterTemp = DatabaseHolder.getInstance().chapterDao().findFirst(chapterId);
                //LitePal.where("chapterId=?", chapterId).findFirst(AudioBookChapter.class);
                    if(chapterTemp != null){
                        if(chapterTemp.getDuration() == 0){
                            long duration = getAudioDurationByUrl(chapterTemp.getUri());
                            chapterTemp.setDuration(duration);
                            DatabaseHolder.getInstance().chapterDao().update(chapterTemp);
                        }
                        curDuration = chapterTemp.getDuration();
                        subscriber.onNext(chapterTemp);
                    }
                })
                .observeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<AudioBookChapter>() {
                    @Override
                    public void onNext(AudioBookChapter re) {
                        callback.next(re.getDuration());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Log.e("test", "onError-" + e.toString());
                    }
                });
    }

    public static long getAudioDurationByUrl(String url){
        long duration = 0L;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.release();
        curDuration = duration;
        return duration;
    }

}
