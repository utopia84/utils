package com.zjmy.mvp.model;

import java.util.List;

public interface ILstener {
    void onError(Throwable e);

    <T> void onSuccess(T result);

    <T> void onSuccess(int indexPage, int pageSize, List<T> result);
}
