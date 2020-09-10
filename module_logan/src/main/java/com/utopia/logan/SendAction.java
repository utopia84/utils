package com.utopia.logan;

class SendAction {

    String uploadPath;

    SendLogRunnable sendLogRunnable;

    boolean isFull;//是否发送全部日志

    boolean isValid() {
        return true;
    }
}
