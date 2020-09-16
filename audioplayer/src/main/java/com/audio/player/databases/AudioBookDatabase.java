package com.audio.player.databases;

import android.app.Application;

import com.audio.player.databases.dao.AudioBookChapterDao;
import com.audio.player.databases.dao.AudioBookProgressDao;
import com.audio.player.databases.table.AudioBookChapter;
import com.audio.player.databases.table.AudioBookProgress;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import eink.yitoa.utils.common.ApplicationUtils;

@Database(entities = {AudioBookProgress.class, AudioBookChapter.class},exportSchema = false, version = 1)
public abstract class AudioBookDatabase extends RoomDatabase {
    private static volatile AudioBookDatabase INSTANCE;

    public abstract AudioBookProgressDao progressDao();
    public abstract AudioBookChapterDao chapterDao();


    public static AudioBookDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (AudioBookDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(ApplicationUtils.getApplication(),
                            AudioBookDatabase.class, DBHelper.DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
