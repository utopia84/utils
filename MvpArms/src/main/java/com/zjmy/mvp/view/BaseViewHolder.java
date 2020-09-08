package com.zjmy.mvp.view;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.zjmy.mvp.presenter.AdapterPresenter;

import java.util.concurrent.TimeUnit;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 这个中间的base层用来做到viewholder与adapter的解耦。
 */
public abstract class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    public int mType;//item的类型
    public View view;

    private final SparseArray<View> mViews;

    private AdapterPresenter adapter;

    //生成viewholder的构造方法。
    public BaseViewHolder(ViewGroup parent, int res, int type) {
        super(LayoutInflater.from(parent.getContext()).inflate(res, parent,false));
        this.mViews = new SparseArray<>();

        this.mType = type;
        view = itemView;
        initView();
    }

    protected abstract void initView();

    public abstract void setData(M data);

    @SuppressWarnings("unchecked")
    protected final <T extends View> T $(@IdRes int id) {
        View v = mViews.get(id);
        if (v == null) {
            v = view.findViewById(id);
            mViews.put(id, v);
        }
        return (T) v;
    }

    /**
     * Will set the text of a TextView.
     *
     * @param view  The view .
     * @param value The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    public BaseViewHolder setText(@NonNull TextView view, @NonNull CharSequence value) {
        view.setText(value);
        return this;
    }

    /**
     * add childView id
     *
     * @param view add the child view id   can support childview click
     * @return if you use adapter bind listener
     * @link {(adapter.setOnItemChildClickListener(listener))}
     * <p>
     * or if you can use  recyclerView.addOnItemTouch(listerer)  wo also support this menthod
     */
    public BaseViewHolder addOnClickListener(@NonNull View view) {
        if (!view.isClickable()) {
            view.setClickable(true);
        }

        RxView.clicks(view)
                .throttleFirst(2L, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    if (adapter != null && adapter.getOnItemChildClickListener() != null) {
                        adapter.getOnItemChildClickListener().onItemChildClick(adapter, view, getLayoutPosition());
                    }
                });
        return this;
    }

    public void setAdapter(AdapterPresenter adapter) {
        this.adapter = adapter;
    }
}
