package com.zjmy.viewbox.target;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.zjmy.viewbox.core.MaskView;
import com.zjmy.viewbox.core.MaskedView;
import com.zjmy.viewbox.util.StateBoxUtil;

public class ViewTarget implements Target {
    protected View mView;//初始界面
    protected MaskView mMaskView;//遮罩层
    private MaskedView maskedView;//被遮罩层


    public ViewTarget(View target , MaskView maskView , MaskedView maskedView) {
        mView = target;
        mMaskView = maskView;
        this.maskedView = maskedView;
    }

    @Override
    public void replaceView() {
        if (StateBoxUtil.checkNotNull(mView,mMaskView)) {
            ViewGroup parentView = (ViewGroup) (mView.getParent());
            final int childIndex = parentView == null ? -1 : parentView.indexOfChild(mView);

            if (childIndex >= 0) {
                parentView.removeViewAt(childIndex);//移除需要被屏蔽的view
                mMaskView.setupSuccessLayout(maskedView);
                parentView.addView(mMaskView, childIndex, mView.getLayoutParams());
            }else{
                mMaskView.setupSuccessLayout(maskedView);
            }
        }
    }

    @Override
    public MaskView getMaskView() {
        return mMaskView;
    }

    @Override
    public View getView() {
        return mView;
    }
}
