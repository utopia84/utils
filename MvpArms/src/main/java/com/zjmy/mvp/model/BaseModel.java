package com.zjmy.mvp.model;


import java.util.List;


public abstract class BaseModel implements IModel {
    private ILstener listener = null;
    protected final Object lock = new Object();
    private ServerApiListener serverApiListener = null;

    public void setServerApiListener(ServerApiListener listener){
        synchronized (lock) {
            serverApiListener = listener;
        }
    }

    public void apiSuccess(String responseString){
        synchronized (lock) {
            if(serverApiListener != null){
                serverApiListener.success(responseString);
            }
        }
    }

    public void apiError(Throwable e){
        synchronized (lock) {
            if(serverApiListener != null){
                serverApiListener.fail(e);
            }
        }
    }

    @Override
    public void setListener(ILstener listener) {
        synchronized (lock) {
            this.listener = listener;
        }
    }

    @Override
    public void removeListener() {
        synchronized (lock) {
            this.listener = null;
        }
    }

    @Override
    public void notifyError(Throwable msg) {
        if (listener != null) {
            listener.onError(msg);
        }
    }

    @Override
    public <T> void notifySuccess(T result) {
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    @Override
    public <T> void notifySuccess(int currentPage, int pageCount, List<T> result) {
        if (listener != null) {
            listener.onSuccess(currentPage, pageCount, result);
        }
    }

    @Override
    public void onPresenterDestory() {
        removeListener();
    }
}
