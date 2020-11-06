package com.zjmy.viewbox.core;

import com.zjmy.viewbox.state.AbstractState;
import com.zjmy.viewbox.target.Target;
import com.zjmy.viewbox.util.StateBoxUtil;

public class StateService {
    private MaskView maskView;

    /**
     * 构造方法
     * @param target 操作目标
     * @param config 默认配置
     */
    StateService(Target target, StateBox.Config config) {
        maskView = target.getMaskView();
        if (StateBoxUtil.checkNotNull(config)) {
            appConfig(config);
        }
    }

    /**
     * 应用系统页面全局配置信息
     * @param config 配置参数
     */
    private void appConfig(StateBox.Config config) {
        if (StateBoxUtil.checkNotNull(maskView)) {
            maskView.addAllState(config.getAbstractStates());
            maskView.show(config.getDefaultPageState());
        }
    }


    /**
     * 隐藏遮罩层
     */
    public void hidden() {
        if (StateBoxUtil.checkNotNull(maskView)) {
            maskView.show(MaskedView.class);
        }
    }

    /**
     * 按照类名显示遮罩层里已有的状态
     * @param state class
     */
    public void show(Class<? extends AbstractState> state) {
        if (StateBoxUtil.checkNotNull(maskView)) {
            maskView.show(state);
        }
    }

    /**
     * 返回遮罩层
     * @return
     */
    public MaskView getMaskView() {
        return maskView;
    }

    /**
     * 往遮罩层里添加页面状态
     * @param state 页面状态
     * @return StateService
     */
    public StateService addStatePage(AbstractState state) {
        if (StateBoxUtil.checkNotNull(maskView)) {
            maskView.addStatePage(state);
        }
        return this;
    }
}
