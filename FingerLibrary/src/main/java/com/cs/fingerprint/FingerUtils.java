package com.cs.fingerprint;

import java.io.File;

public class FingerUtils {
    public static final String FINGER_PATH = "data/data/com.zjmy.eink/files";

    public static void clearFingerByUserId(String userId){
        File file = new File(FINGER_PATH, userId + ".bin");
        if (file.exists()) {
            file.delete();
        }
    }
}
