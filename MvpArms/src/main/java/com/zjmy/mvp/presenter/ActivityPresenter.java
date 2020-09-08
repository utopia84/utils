package com.zjmy.mvp.presenter;

import android.os.Bundle;
import android.util.Log;

import com.zjmy.mvp.model.ILstener;
import com.zjmy.mvp.model.IModel;
import com.zjmy.mvp.view.IView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import eink.yitoa.utils.EinkRefreshMode;


public abstract class ActivityPresenter<V extends IView, M extends IModel> extends AppCompatActivity implements
        ILstener{
    private V v;//View 层
    private M m;//Model 层

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //onCreate中实现
        super.onCreate(savedInstanceState);
        try {
            m = getRootModelClass().newInstance();
            m.setListener(this);

            v = getRootViewClass().newInstance();
            v.creatView(getLayoutInflater(), null, savedInstanceState);
            v.setActivityContext(getContext());
            setContentView(v.getRootView());
            v.initView();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        bindEvenListener();
        inCreat(savedInstanceState);
    }

    //解除绑定
    @Override
    protected void onDestroy() {
        inDestory();

        try {
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
        } catch (Exception e) {
            Log.e("test", "onDestroy not effective!");
        }

        super.onDestroy();
    }

    /**
     * 获取viwe的引用,若view 已被回收，则抛出异常
     *
     * @return
     */
    public V getViewRef() {
        if (v == null) {
            finish();
        }
        return v;
    }

    public M getModelRef(){
        if (m == null) {
            finish();
        }
        return m;
    }

    public abstract Class<V> getRootViewClass();

    public abstract Class<M> getRootModelClass();

    public abstract void inCreat(Bundle savedInstanceState);

    public abstract void inDestory();

    public abstract AppCompatActivity getContext();

    protected void bindEvenListener() {
    }


    public void onError(Throwable e) {
        if (v == null) {
            finish();
        }
    }

    public <T> void onSuccess(T result) {
        if (v == null) {
            finish();
        }
    }

    public <T> void onSuccess(int indexPage, int pageSize, List<T> result) {
        if (v == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EinkRefreshMode.updateToLocalRefreshMode();
    }
}