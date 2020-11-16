package com.zjmy.viewbox.core;

import android.app.Activity;
import android.view.View;
import com.zjmy.viewbox.state.BaseStateView;
import com.zjmy.viewbox.target.Target;
import com.zjmy.viewbox.target.ViewTarget;
import java.util.ArrayList;
import java.util.List;

public class StateBox {
    private final static StateBox STATE_BOX = new StateBox();
    private Config config = new Config();

    public static StateBox getDefault() {
        return STATE_BOX;
    }

    private StateBox() {}

    public Config getConf() {
        return config;
    }

    public StateService register(View target , OnReloadListener listener) {
        MaskView maskView = new MaskView(target, listener);//构造遮罩层
        BaseStateView maskedView = new MaskedView(target, listener);//构造被遮罩层

        Target targetContext = new ViewTarget(target, maskView , maskedView);
        targetContext.replaceView();
        return new StateService(targetContext , config);
    }

    public StateService register(Activity activity , int viewId , OnReloadListener listener) {
        View target = activity.findViewById(viewId);
        return register(target,listener);
    }


    /**
     * 全局配置
     */
    public static class Config {
        private List<BaseStateView> states = new ArrayList<>();//非正常数据展示页面集合
        private Class<? extends BaseStateView> defaultPageState = null;//初始默认界面

        public Config addState( BaseStateView state) {
            states.add(state);
            return this;
        }

        public Config addState(BaseStateView state , boolean isDefault) {
            states.add(state);
            if (isDefault){
                this.defaultPageState = state.getClass();
            }
            return this;
        }

        List<BaseStateView> getAbstractStates() {
            return states;
        }

        Class<? extends BaseStateView> getDefaultPageState() {
            return defaultPageState;
        }
    }

    public interface OnReloadListener {
        void onReload(View v);
    }
}
