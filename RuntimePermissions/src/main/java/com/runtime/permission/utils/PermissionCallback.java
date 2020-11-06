package com.runtime.permission.utils;

import android.content.Context;

public interface PermissionCallback {
    void granted(int requestCode);//同意

    void denied(Context context, int requestCode) ;//拒绝

    void neverAsk(Context context,int requestCode) ;//拒绝后,不在询问

    void cancel(Context context,int requestCode) ;//取消
}
