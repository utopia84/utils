package com.runtime.permission.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.runtime.permission.utils.PermissionCallback;
import com.runtime.permission.utils.PermissionUtils;

public class PermissionFragment extends Fragment {
    private int mRequestCode;
    private PermissionCallback mCallback;

    public PermissionFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //置改变时，Fragment不会被重新创建
        setRetainInstance(true);
    }

    /**
     * 处理请求系统授权
     *
     * @param permissions 待授权权限列表
     * @param requestCode 授权返回码
     * @param callback    授权回调
     */
    public void request(String[] permissions, int requestCode, PermissionCallback callback) {
        this.mCallback = callback;
        this.mRequestCode = requestCode;

        if (PermissionUtils.hasSelfPermissions(getActivity(), permissions)) {
            mCallback.granted(requestCode);
        } else {
            requestPermissions(permissions, requestCode);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestCode == requestCode && mCallback != null) {
            //授权
            if (PermissionUtils.verifyPermissions(grantResults)) {
                mCallback.granted(mRequestCode);
                return;
            }

            //拒绝
            FragmentActivity context = getActivity();
            if (PermissionUtils.shouldShowRequestPermissionRationale(context, permissions)) {
                //当用户之前已经请求过该权限并且拒绝了授权
                mCallback.denied(context,mRequestCode);
            } else {
                //用户勾选了不再提醒
                mCallback.neverAsk(context,mRequestCode);
            }

        }
    }

}
