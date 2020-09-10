package com.zjmy.mvp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

/**
 * View delegate base class
 * 视图层代理的接口协议
 */
public interface IView {

    /**
     * 加载根视图
     * @param inflater
     * @param parent
     * @param bundle
     */
    void creatView(LayoutInflater inflater, ViewGroup parent, Bundle bundle);

    /**
     * 返回根视图给presenter
     * @return
     */
    View getRootView();

    /**
     * 返回根视图的id
     * @return
     */
    int getRootViewId();

    /**
     * 初始化组件，默认使用butterKinfe
     */
    void initView();

    /**
     * 解除butterkinfe的绑定
     */
    void removeView();

    /**
     * 在presenter里面返回自己的context给view层
     */
    void setActivityContext(FragmentActivity activity);

    /**
     * 在presenter销毁的时候调用,生命周期同步一下,有时候需要在view释放什么
     */
    void onPresenterDestory();


    /**
     * 更新分页数据
     * @param currentPage
     * @param pageCount
     * @param result
     * @param <T>
     */
    <T> void notifyDataSetChanged(int currentPage, int pageCount, List<T> result);

    /**
     * 更新实体数据
     * @param result
     * @param <T>
     */
    <T> void notifyDataSetChanged(T result);

    /**
     * 显示加载中页面
     */
    void showLoading();

    /**
     * 显示错误界面
     * @param e
     */
    void notifyError(Throwable e);

    void invalidate();
}
