package com.utopia.logan;

import android.text.TextUtils;

import java.io.File;

class WriteAction {

    String log; //日志

    LogType type;//日志类型

    public WriteAction(LogType type,String log) {
        this.log = log;
        this.type = type;
    }

    boolean isValid() {
        boolean valid = false;
        if (!TextUtils.isEmpty(log)) {
            valid = true;
        }
        return valid;
    }
}
