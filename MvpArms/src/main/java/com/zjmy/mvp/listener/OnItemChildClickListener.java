package com.zjmy.mvp.listener;

import android.view.View;
import com.zjmy.mvp.presenter.AdapterPresenter;

/**
 * item里面嵌套内容点击事件
 */
public interface OnItemChildClickListener {
    void onItemChildClick(AdapterPresenter<?> adapter, View view, int position);
}
