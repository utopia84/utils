package com.audio.player.databases.dao;

import com.audio.player.databases.table.AudioBookProgress;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AudioBookProgressDao {
    @Query("SELECT * FROM tb_audio_book_progress")
    List<AudioBookProgress> loadAll();
}
