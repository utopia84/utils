package me.wcy.htmltext.html;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public interface HtmlImageLoader {

    /**
     * 图片加载回调
     * */
    interface LoadCallback{
        /**
         * 准备加载
         * */
        void onPrepare();

        /**
         * 加载成功
         * */
        void onComplete(Bitmap bitmap);

        /**
         * 加载失败
         * */
        void onFailed(Throwable e);
    }

    /**
     * 加载图片
     * */
    void loadImage(String url, LoadCallback callback);

    /**
     * 加载中的占位图
     * */
    Drawable getPlaceHolderDrawable();

    /**
     * 加载失败的占位图
     * */
    Drawable getFailureDrawable();

    /**
     * TextView中显示图片的最大宽度
     *
     * */
    int getMaxWidth();

    /**
     * 是否强制图片按照最大宽度{@link #getMaxWidth()}显示
     *
     * @return true 强制执行，需要设置{@link #getMaxWidth()}
     * */
    boolean fitWidth();
}
