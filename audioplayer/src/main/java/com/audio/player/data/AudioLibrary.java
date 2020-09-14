package com.audio.player.data;

import android.media.MediaPlayer;
import android.util.Log;

import com.audio.player.data.db.AudioBookChapter;
import com.audio.player.model.BaseSubscriber;

import org.litepal.LitePal;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @作者 邸昌顺
 * @时间 2019/3/18 13:46
 * @描述 处理音频数据
 */
public class AudioLibrary {

    public interface Callback{
       <T> void next(T t);
    }

    public static long curDuration;

    public static String getRoot(){
        return "root_eink";//随便定义
    }

    public static void notifyAudioDuration(String mediaId, long duration, Callback callback){
        Observable.create((Subscriber<? super AudioBookChapter> subscriber) -> {
                    AudioBookChapter chapter = LitePal.where("chapterId=?", mediaId).findFirst(AudioBookChapter.class);
                    if(chapter != null){
                        Log.e("test", "duration=" + chapter.getDuration() + ", " + duration);
                        chapter.setDuration(duration);
                        chapter.saveOrUpdate("chapterId=?", mediaId);
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

                AudioBookChapter chapterTemp = LitePal.where("chapterId=?", chapterId).findFirst(AudioBookChapter.class);
                    if(chapterTemp != null){
                        if(chapterTemp.getDuration() == 0){
                            long duration = getAudioDurationByUrl(chapterTemp.getUri());
                            chapterTemp.setDuration(duration);
                            chapterTemp.saveOrUpdate("chapterId=?", chapterTemp.getChapterId());
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
