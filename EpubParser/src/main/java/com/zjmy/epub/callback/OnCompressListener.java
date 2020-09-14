package com.zjmy.epub.callback;

import android.graphics.Bitmap;

public abstract class OnCompressListener {

  /**
   * Fired when the compression is started, override to handle in your own code
   */
  public void onStart(){}

  /**
   * Fired when a compression returns successfully, override to handle in your own code
   */
  public void onSuccess(Bitmap bitmap){}

  /**
   * Fired when a compression fails to complete, override to handle in your own code
   */
  public void onError(){}
}
