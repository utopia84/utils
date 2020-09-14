package com.audio.player.listener;

import com.audio.player.client.MediaSeekBar;

import java.util.HashMap;
import java.util.Map;

public class SeekBarEventBus {

    private static Map<String, MediaSeekBar> mapBus=new HashMap<>();

    public static void setObject(MediaSeekBar object,String key){
        mapBus.put(key,object);
    }

    public static MediaSeekBar getObject(String key){
        return mapBus.get(key);
    }

    public static void removeObject(String key){
        mapBus.remove(key);
    }
}
