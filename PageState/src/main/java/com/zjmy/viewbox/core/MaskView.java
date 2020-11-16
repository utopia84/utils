package com.zjmy.viewbox.core;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.zjmy.viewbox.util.StateBoxUtil;
import com.zjmy.viewbox.state.BaseStateView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class MaskView extends FrameLayout {
    private Map<Class<? extends BaseStateView>, BaseStateView> callbacks = new HashMap<>();

    private Class<? extends BaseStateView> preCallback;
    private StateBox.OnReloadListener reloadListener;

    public MaskView(View view , StateBox.OnReloadListener reloadListener) {
        super(view.getContext());
        this.reloadListener = reloadListener;
    }


    public void setupSuccessLayout(BaseStateView state) {
        addStatePage(state);
        View rootView = state.getRootView();
        rootView.setVisibility(View.INVISIBLE);

        addView(rootView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addAllState(List<BaseStateView> states) {
        if (states != null && !states.isEmpty()) {
            for (BaseStateView state : states) {
                addStatePage(state);
            }
        }
    }

    public void addStatePage(BaseStateView state) {
        if (state != null ) {
            state.setOnReloadListener(getContext(), reloadListener);
            callbacks.put(state.getClass(), state);
        }
    }

    public void show(final Class<? extends BaseStateView> state) {
        if (state != null) {
            if (StateBoxUtil.isMainThread()) {
                showWithMainThread(state);
            } else {
                post(()->showWithMainThread(state));
            }
        }
    }


    private void showWithMainThread(Class<? extends BaseStateView> status) {

        if (preCallback == status) {//重复调用
            return;
        }

        //销毁上一个页面
        BaseStateView preBaseStateView = callbacks.get(preCallback);
        if (preBaseStateView != null) {
            preBaseStateView.onDetach();
        }

        //清理容器页面元素
        if (getChildCount() > 1) {
            removeViewAt(1);
        }

        MaskedView maskedView = (MaskedView) callbacks.get(MaskedView.class);
        BaseStateView currentBaseStateView = callbacks.get(status);
        if (status == MaskedView.class && maskedView != null){
            //显示被遮罩层
            maskedView.show();
        }else if (currentBaseStateView != null && maskedView != null){
            maskedView.hide();
            View rootView = currentBaseStateView.getRootView();
            addRootView(rootView);
            currentBaseStateView.onAttach(getContext(), rootView);
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
