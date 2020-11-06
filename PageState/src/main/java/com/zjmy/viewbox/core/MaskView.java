package com.zjmy.viewbox.core;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zjmy.viewbox.listener.OnReloadListener;
import com.zjmy.viewbox.util.StateBoxUtil;
import com.zjmy.viewbox.state.AbstractState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class MaskView extends FrameLayout {
    private Map<Class<? extends AbstractState>, AbstractState> callbacks = new HashMap<>();

    private Class<? extends AbstractState> preCallback;
    private OnReloadListener reloadListener;

    public MaskView(View view , OnReloadListener reloadListener) {
        super(view.getContext());
        this.reloadListener = reloadListener;
    }


    public void setupSuccessLayout(AbstractState state) {
        addStatePage(state);
        View rootView = state.getRootView();
        rootView.setVisibility(View.INVISIBLE);

        addView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addAllState(List<AbstractState> states) {
        if (states != null && !states.isEmpty()) {
            for (AbstractState state : states) {
                addStatePage(state);
            }
        }
    }

    public void addStatePage(AbstractState state) {
        if (state != null ) {
            state.setOnReloadListener(getContext(), reloadListener);
            callbacks.put(state.getClass(), state);
        }
    }

    public void show(final Class<? extends AbstractState> state) {
        if (state != null) {
            if (StateBoxUtil.isMainThread()) {
                showWithMainThread(state);
            } else {
                post(()->showWithMainThread(state));
            }
        }
    }


    private void showWithMainThread(Class<? extends AbstractState> status) {

        if (preCallback == status) {//重复调用
            return;
        }

        //销毁上一个页面
        AbstractState preAbstractState = callbacks.get(preCallback);
        if (preAbstractState != null) {
            preAbstractState.onDetach();
        }

        //清理容器页面元素
        if (getChildCount() > 1) {
            removeViewAt(1);
        }

        MaskedView maskedView = (MaskedView) callbacks.get(MaskedView.class);
        AbstractState currentAbstractState = callbacks.get(status);
        if (status == MaskedView.class && maskedView != null){
            //显示被遮罩层
            maskedView.show();
        }else if (currentAbstractState != null && maskedView != null){
            maskedView.hide();
            View rootView = currentAbstractState.getRootView();
            addRootView(rootView);
            currentAbstractState.onAttach(getContext(), rootView);
        }

        preCallback = status;
    }

    private void addRootView(View rootView) {
        ViewGroup parentView = (ViewGroup)rootView.getParent();
        if (StateBoxUtil.checkNotNull(rootView)) {
            final int childIndex = parentView == null ? -1 : parentView.indexOfChild(rootView);

            if (childIndex >= 0) {
                parentView.removeViewAt(childIndex);
            }

            addView(rootView);
        }
    }
}
