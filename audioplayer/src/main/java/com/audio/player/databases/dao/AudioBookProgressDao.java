package com.audio.player.databases.dao;

import com.audio.player.databases.DBHelper;
import com.audio.player.databases.table.AudioBookChapter;
import com.audio.player.databases.table.AudioBookProgress;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Flowable;

@Dao
public interface AudioBookProgressDao {
    @Query("SELECT * FROM "+ DBHelper.TABLE_AUDIO_PROGRESS)
    Flowable<List<AudioBookProgress>>  loadAll();

    @Query("SELECT * FROM "+DBHelper.TABLE_AUDIO_PROGRESS+" WHERE chapterId = :chapterId AND userid = :userId")
    Flowable<List<AudioBookProgress>> find(String chapterId, String userId);

    //存在则替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<AudioBookProgress> progresses);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveOrUpdate(AudioBookProgress chapter);

    @Query("DELETE FROM " + DBHelper.TABLE_AUDIO_PROGRESS)
    void deleteAll();

    @Query("DELETE FROM "+DBHelper.TABLE_AUDIO_PROGRESS+" WHERE chapterId = :chapterId AND userid = :userId")
    void delete(String chapterId, String userId);
}
