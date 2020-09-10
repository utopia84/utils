package com.zjmy.mvp.view;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentActivity;
import eink.yitoa.utils.EinkRefreshMode;

/**
 * 将view加载的过程写在抽象类，做到代码复用。
 * View delegate base class
 * 视图层代理的基类
 */

public abstract class BaseView implements IView {
    protected FragmentActivity activity;
    private int refreshNum = 1;//加刷新，如果一个界面连刷5次，则需要全刷一次

    private View rootView;
    private SparseArray<View> mViews = new SparseArray<View>();

    public void creatView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        int resourceId = getRootViewId();

        if (resourceId == 0) {
            throw new RuntimeException("rootview's id can't be null");
        }

        rootView = inflater.inflate(resourceId, parent, false);
        rootView.setClickable(true);
    }

    public final View getRootView() {
        return rootView;
    }

    public abstract int getRootViewId();

    public final void removeView() {
        if (mViews != null) {
            mViews.clear();
        }
        mViews = null;
        rootView = null;
    }

    public abstract void setActivityContext(FragmentActivity activity);

    @SuppressWarnings("unchecked")
    protected final <T extends View> T bindView(@IdRes int id) {
        T view2 = (T) mViews.get(id);
        if (view2 == null) {
            view2 = (T) rootView.findViewById(id);
            mViews.put(id, view2);
        }
        return view2;
    }

    @SuppressWarnings("unchecked")
    public <T extends FragmentActivity> T getActivity() {
        return (T) rootView.getContext();
    }

    @SuppressWarnings("unchecked")
    public final <T extends View> T get(@IdRes int id) {
        return (T) bindView(id);
    }

    public void setOnClickListener(View.OnClickListener listener, @IdRes int... ids) {
        if (ids == null) {
            return;
        }
        for (int id : ids) {
            get(id).setOnClickListener(listener);
        }
    }

    public <T> void notifyDataSetChanged(int currentPage, int pageCount, List<T> result) {
        if (++refreshNum > 5) {
            refreshNum = 1;
            invalidate();
        }
    }

    /**
     * 强制刷新当前activity View
     */
    @Override
    public void invalidate() {
        if (rootView != null) {
            EinkRefreshMode.updateToFullRefreshMode();
            rootView.invalidate();
            EinkRefreshMode.updateToLocalRefreshMode();
        }
    }

    public <T> void notifyDataSetChanged(T result) {
        if (++refreshNum > 5) {
            refreshNum = 1;
            invalidate();
        }
    }

    public void showLoading() {
    }

    public void notifyError(Throwable e) {
    }

    @Override
    public void onPresenterDestory() {
        removeView();
        activity = null;
    }
}
