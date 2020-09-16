package com.audio.player.databases.dao;

import com.audio.player.databases.DBHelper;
import com.audio.player.databases.table.AudioBookChapter;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface AudioBookChapterDao {
    @Query("SELECT * FROM "+ DBHelper.TABLE_AUDIO_CHAPTER+" WHERE chapterId = :chapterId LIMIT 1")
    Flowable<AudioBookChapter> findFirst(String chapterId);

    @Query("SELECT * FROM "+DBHelper.TABLE_AUDIO_CHAPTER)
    Flowable<List<AudioBookChapter>> loadAll();

    @Update
    void update(AudioBookChapter audioBookChapter);

    //存在则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveAll(List<AudioBookChapter> chapters);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveOrUpdate(AudioBookChapter chapter);

    @Query("DELETE FROM "+DBHelper.TABLE_AUDIO_CHAPTER)
    void deleteAll();
}
