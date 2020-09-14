package com.zjmy.epub.callback;

import com.zjmy.epub.bean.EpubBookInfo;

public abstract class OnResolveEpubInfoCallback {

  public void onSuccess(EpubBookInfo info){}

  public void onError(Throwable e){}
}
