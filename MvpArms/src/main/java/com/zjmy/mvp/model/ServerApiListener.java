package com.zjmy.mvp.model;

/**
 * @作者 邸昌顺
 * @时间 2019/4/11 14:11
 * @描述
 */
public interface ServerApiListener {

    void start();

    void success(String dataString);

    void fail(Throwable e);

    void setTip(String tip);
}
