package com.audio.player.databases;

import androidx.room.Room;
import eink.yitoa.utils.common.ApplicationUtils;

public class DatabaseHolder {
    private volatile static AudioBookDatabase mDatabase;

    public static AudioBookDatabase getInstance(){
        if (mDatabase == null){
            synchronized (AudioBookDatabase.class){
                if (mDatabase == null){
                    mDatabase = Room.databaseBuilder(ApplicationUtils.getApplication(), AudioBookDatabase.class, "db_audio_book")
                            .build();
                }
            }
        }
        return mDatabase;
    }
}
