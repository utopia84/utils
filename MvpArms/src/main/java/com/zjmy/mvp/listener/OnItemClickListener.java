package com.zjmy.mvp.listener;

import com.zjmy.mvp.view.BaseViewHolder;

/**
 * item点击事件
 */
public  interface OnItemClickListener {

    /**
     * 单击事件
     */
    void onClick(BaseViewHolder<?> holder, int position);

    /**
     * 长按事件
     */
    default void onLongClick(BaseViewHolder<?> holder, int position) {

    }
}
