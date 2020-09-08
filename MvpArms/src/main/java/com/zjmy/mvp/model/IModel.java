/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zjmy.mvp.model;

import java.util.List;

/**
 * 用作双向绑定使用
 * Do Model-View two-way binding future use
 *
 */
public interface IModel {

    void removeListener();

    void setListener(ILstener listener);

    void notifyError(Throwable msg);

    <T> void notifySuccess(T result);

    <T> void notifySuccess(int currentPage, int pageCount, List<T> result);
    /**
     * 在presenter销毁的时候调用,生命周期同步一下,有时候需要在view释放什么
     */
    void onPresenterDestory();

}
