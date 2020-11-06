package com.zjmy.viewbox.state;

import android.content.Context;
import android.view.View;

import com.zjmy.viewbox.listener.OnReloadListener;
import com.zjmy.viewbox.util.StateBoxUtil;
import java.io.Serializable;

public abstract class AbstractState {
    private View rootView;
    private Context context;
    private OnReloadListener onReloadListener;

    public AbstractState() {
    }

    public AbstractState(View view, Context context, OnReloadListener onReloadListener) {
        this.rootView = view;
        this.context = context;
        this.onReloadListener = onReloadListener;
    }

    public void setOnReloadListener(Context context, OnReloadListener onReloadListener) {
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
