package com.zjmy.viewbox.target;

import android.content.Context;
import android.view.View;
import com.zjmy.viewbox.core.MaskView;

public interface Target {
    void replaceView();

    MaskView getMaskView();

    View getView();
}
