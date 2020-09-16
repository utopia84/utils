package com.audio.player.data;

import android.media.MediaPlayer;
import com.audio.player.databases.AudioBookDatabase;
import com.audio.player.databases.dao.AudioBookChapterDao;
import com.audio.player.listener.Callback;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AudioLibrary {
    private final AudioBookChapterDao mChapterDao;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public AudioLibrary() {
        mChapterDao = AudioBookDatabase.getInstance().chapterDao();
    }

    public static String getRoot(){
        return "root_eink";//随便定义
    }

    //更新章节音频播放总时长
    public void updateAudioDuration(String chapterId, long duration, Callback<Long> callback){
        mDisposable.add(mChapterDao.findFirst(chapterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(audioBookChapter -> {
                    audioBookChapter.setDuration(duration);
                    mChapterDao.update(audioBookChapter);
                })
                .subscribe(audioBookChapter -> callback.finished(duration),Throwable::printStackTrace)
        );
    }

    //获取章节时长
    public void getAudioDuration(String chapterId, Callback<Long> callback){
        mDisposable.add(mChapterDao.findFirst(chapterId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(audioBookChapter -> callback.finished(audioBookChapter.getDuration()),Throwable::printStackTrace)
        );
    }

    //跟据网络资源，解析音频时长
    public long getAudioDurationByUrl(String url){
        long duration = 0L;
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mediaPlayer.release();
        }

        return duration;
    }

    /**
     * 释放资源
     */
    public void release(){
        mDisposable.clear();
    }
}
