package com.zjmy.viewbox.core;

import android.view.View;

import com.zjmy.viewbox.state.BaseStateView;

public class MaskedView extends BaseStateView {
    public MaskedView(View view, StateBox.OnReloadListener onReloadListener) {
        super(view, view.getContext(), onReloadListener);
    }

    @Override
    protected int onCreateView() {
        return 0;
    }

    public void hide() {
        obtainRootView().setVisibility(View.INVISIBLE);
    }

    public void show() {
        obtainRootView().setVisibility(View.VISIBLE);
    }

}
