package com.zjmy.mvp.presenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjmy.mvp.model.BaseModel;
import com.zjmy.mvp.model.ILstener;
import com.zjmy.mvp.view.BaseView;
import java.util.List;

import androidx.fragment.app.Fragment;

/**
 * onAttach -> onCreate -> onCreateView ->onViewCreated -> onActivityCreated-> onViewStateRestored -> onStart -> onResume
 *
 * @param <T>
 */
public abstract class FragmentPresenter<T extends BaseView, M extends BaseModel> extends Fragment implements ILstener {
    private T v;//View 层
    private M m;//Model 层

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            v = getRootViewClass().newInstance();
            m = getRootModelClass().newInstance();

            v.setActivityContext(getActivity());
            m.setListener(this);
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        v.creatView(inflater, container, savedInstanceState);
        return v.getRootView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        v.initView();
        bindEvenListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (v != null) {
            v.removeView();
            v.onPresenterDestory();
            v = null;
        }

        if (m != null) {
            m.onPresenterDestory();
            m.removeListener();
            m = null;
        }
    }

    /**
     * 获取viwe的引用,若view 已被回收，则抛出异常
     *
     * @return T
     */
    public T getViewRef() throws RuntimeException {
        if (v != null) {
            return v;
        } else {
            throw new RuntimeException("rootview can't be null");
        }
    }

    public M getModelRef() throws RuntimeException {
        if (m != null) {
            return m;
        } else {
            throw new RuntimeException("rootmodel's id can't be null");
        }
    }

    public abstract Class<T> getRootViewClass();

    public abstract Class<M> getRootModelClass();

    protected void bindEvenListener() {

    }

    public void onError(Throwable e) {
        if (e != null && v != null) {
            v.notifyError(e);
        }
    }

    public <T> void onSuccess(T result) {

    }

    public <T> void onSuccess(int indexPage, int pageSize, List<T> result) {

    }
}
