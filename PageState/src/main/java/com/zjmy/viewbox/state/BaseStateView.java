package com.zjmy.viewbox.state;

import android.content.Context;
import android.view.View;

import com.zjmy.viewbox.core.StateBox;
import com.zjmy.viewbox.util.StateBoxUtil;

public abstract class BaseStateView {
    private View rootView;
    private Context context;
    private StateBox.OnReloadListener onReloadListener;

    public BaseStateView(View view, Context context, StateBox.OnReloadListener onReloadListener) {
        this.rootView = view;
        this.context = context;
        this.onReloadListener = onReloadListener;
    }

    public void setOnReloadListener(Context context, StateBox.OnReloadListener onReloadListener) {
        this.context = context;
        this.onReloadListener = onReloadListener;
    }

    public View getRootView() {
        int resId = onCreateView();
        if (resId == 0 && rootView != null) {
            return rootView;
        }

        if (onBuildView(context) != null) {
            rootView = onBuildView(context);
        }

        if (rootView == null) {
            rootView = View.inflate(context, onCreateView(), null);
        }

        rootView.setOnClickListener(v -> {
            if (StateBoxUtil.checkNotNull(onReloadListener)) {
                onReloadListener.onReload(v);
            }
        });
        onViewCreate(context, rootView);
        return rootView;
    }

    protected View onBuildView(Context context) {
        return null;
    }


    public View obtainRootView() {
        if (rootView == null) {
            rootView = View.inflate(context, onCreateView(), null);
        }
        return rootView;
    }

    protected void onViewCreate(Context context, View view) {
    }

    protected abstract int onCreateView();

    public void onAttach(Context context, View view){}

    public void onDetach() {}
}
