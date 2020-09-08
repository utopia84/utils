package com.zjmy.mvp.presenter;

import android.content.Context;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.zjmy.mvp.listener.OnItemChildClickListener;
import com.zjmy.mvp.listener.OnItemClickListener;
import com.zjmy.mvp.view.BaseViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 所有的数据管理都放在BaseAdapter里面。
 * 由于这个适配器并不提供添加header和foot的功能，所以getItemCount()最好还是要开发者自己实现
 */
public abstract class AdapterPresenter<M> extends RecyclerView.Adapter<BaseViewHolder> {
    protected Context mContext;
    //item数据的model集合
    protected List<M> mDatas;
    //开发者添加的头数量
    private int headNum = 0;
    //开发者添加的尾的数量
    private int footNum = 0;

    //item的点击监听接口
    protected OnItemClickListener onItemClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;

    //用来提供同步代码块的锁
    private final Object mLock = new Object();


//-------------------------------构造器-----------------------------------

    public OnItemChildClickListener getOnItemChildClickListener() {
        return mOnItemChildClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener mOnItemChildClickListener) {
        this.mOnItemChildClickListener = mOnItemChildClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * 不传入数据
     *
     * @param mContext
     */
    public AdapterPresenter(@NonNull Context mContext) {
        this(mContext, new ArrayList<M>());
    }

    /**
     * 传入数组类型的数据
     *
     * @param mContext
     * @param mDatas
     */
    public AdapterPresenter(@NonNull Context mContext, @NonNull M[] mDatas) {
        this(mContext, Arrays.asList(mDatas));
    }

    /**
     * 传入List类型的数据
     *
     * @param mContext
     * @param mDatas
     */
    public AdapterPresenter(@NonNull Context mContext, @NonNull List<M> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }


//---------------------------对继承函数的重写--------------------------------

    /**
     * 负责承载每个子项的布局。将viewType返回,具体的实现由开发者去做。
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = OnCreatViewHolder(parent, viewType);
        viewHolder.setAdapter(this);
        return viewHolder;
    }

    public abstract BaseViewHolder OnCreatViewHolder(ViewGroup parent, int viewType);

    /**
     * 负责将每个子项holder绑定数据。默认框架实现点击事件接口的回调
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
        holder.setData(getItem(position));

        //设置监听接口
        if (onItemClickListener != null) {
            RxView.clicks(holder.itemView)
                    .throttleFirst(1L, TimeUnit.SECONDS)
                    .subscribe(aVoid -> {
                        onItemClickListener.onClick(holder, position);
                    });
            holder.itemView.setOnLongClickListener(v -> {
                onItemClickListener.onLongClick(holder, position);
                return true;
            });
        }
    }

    protected void setListener(BaseViewHolder holder, int position){
        //设置监听接口
        if (onItemClickListener != null) {
            RxView.clicks(holder.itemView)
                    .throttleFirst(1L, TimeUnit.SECONDS)
                    .subscribe(aVoid -> {
                        onItemClickListener.onClick(holder, position);
                    });
            holder.itemView.setOnLongClickListener(v -> {
                onItemClickListener.onLongClick(holder, position);
                return true;
            });
        }
    }

    /**
     * 将datas中的某个数据返回给viewholder
     * 前提是在个position不是head不是foot,但是这个判断应该是开发者去实现。
     * 所以这里默认开发者调用的时候已经不是head和foot，所以直接根据开发提供的position减去headNum给数据
     *
     * @param position
     * @return
     */
    public M getItem(int position) {
        if (position - headNum < mDatas.size()) {
            return mDatas.get(position - headNum);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + setHeadNum() + setFootNum();
    }

    //开发者去实现，告诉BaseAdapter添加了几个head或者foot
    public int setHeadNum() {
        return 0;
    }

    public int setFootNum() {
        return 0;
    }

// ----------------------------对数据源的操作--------------------------------

    /**
     * 将数据添加到集合最末尾
     *
     * @param data
     */
    public void add(M data) {
        add(data, mDatas.size());
    }

    /**
     * 将数据添加到指定位置
     *
     * @param data
     * @param position
     */
    public void add(M data, int position) {
        if (data != null) {
            synchronized (mLock) {
                mDatas.add(position, data);
            }
        }
    }

    public void setDatasAndRefresh(Collection<? extends M> collection) {
        synchronized (mLock) {
            if (mDatas != null) {
                mDatas.clear();
                mDatas.addAll(collection);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 将一个集合添加到数据最尾端
     *
     * @param collection
     */
    public void addAll(Collection<? extends M> collection) {
        addAll(collection, mDatas.size());
    }

    public void addAll(Collection<? extends M> collection, int position) {
        if (collection != null && collection.size() != 0 && position >= 0) {
            synchronized (mLock) {
                mDatas.addAll(position, collection);
            }
        }
    }

    /**
     * 删除集合最尾部数据
     */
    public void remove() {
        synchronized (mLock) {
            remove(mDatas.size() - 1);
        }
    }

    /**
     * 删除指定位置的数据
     *
     * @param position
     */
    public void remove(int position) {
        if (position >= 0 && position <= (mDatas.size() - 1)) {
            synchronized (mLock) {
                mDatas.remove(position);
            }
        }
    }

    /**
     * 删除一个特定数据的元素
     *
     * @param data
     */
    public void remove(M data) {
        if (data != null) {
            synchronized (mLock) {
                mDatas.remove(data);
            }
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        synchronized (mLock) {
            mDatas.clear();
        }
    }

    /**
     * 回收垃圾
     */
    public void onDestory() {
        if (mDatas != null) {
            mDatas.clear();
            mDatas = null;
        }
    }
}
