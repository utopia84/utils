package com.zjmy.viewbox.core;

import android.app.Activity;
import android.view.View;

import com.zjmy.viewbox.listener.OnReloadListener;
import com.zjmy.viewbox.state.AbstractState;
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
        MaskedView maskedView = new MaskedView(target, listener);//构造被遮罩层

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
        private List<AbstractState> states = new ArrayList<>();//非正常数据展示页面集合
        private Class<? extends AbstractState> defaultPageState = null;//初始默认界面

        public Config addState( AbstractState state) {
            states.add(state);
            return this;
        }

        public Config addState(AbstractState state , boolean isDefault) {
            states.add(state);
            if (isDefault){
                this.defaultPageState = state.getClass();
            }
            return this;
        }

        List<AbstractState> getAbstractStates() {
            return states;
        }

        Class<? extends AbstractState> getDefaultPageState() {
            return defaultPageState;
        }
    }
}
