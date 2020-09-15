package com.audio.player.databases.dao;

import com.audio.player.databases.table.AudioBookChapter;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AudioBookChapterDao {
    @Query("SELECT * FROM tb_audio_book_chapter WHERE chapterId = :chapterId")
    AudioBookChapter findFirst(String chapterId);

    @Query("SELECT * FROM tb_audio_book_chapter")
    List<AudioBookChapter> loadAll();

    @Update
    void update(AudioBookChapter audioBookChapter);
}
