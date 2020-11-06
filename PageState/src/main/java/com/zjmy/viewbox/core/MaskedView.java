package com.zjmy.viewbox.core;

import android.view.View;

import com.zjmy.viewbox.listener.OnReloadListener;
import com.zjmy.viewbox.state.AbstractState;

public class MaskedView extends AbstractState {
    public MaskedView(View view, OnReloadListener onReloadListener) {
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
