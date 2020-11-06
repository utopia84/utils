package com.runtime.permission.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.runtime.permission.fragment.PermissionFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

public class FragmentUtils {
    /**
     * 根据上下文环境，生成一个代理授权的Fragment。
     * @param context 上下文
     * @param tag Fragment标签
     * @return PermissionFragment实例
     */
    public static PermissionFragment getPermissionFragment(@Nullable LifecycleOwner context , @Nullable String tag) {
        PermissionFragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager(context);

        if (fragmentManager != null) {
            fragment = findPermissionFragment(fragmentManager, tag);

            if (fragment == null) {
                fragment = new PermissionFragment();
                fragmentManager
                        .beginTransaction()
                        .add(fragment, tag)
                        .commitNowAllowingStateLoss();
            }
        }
        return fragment;
    }

    /**
     * 根据上下文环境(Activity或者Fragment)去获取FragmentManager
     * @param context 上下文
     * @return FragmentManager
     */
    private static FragmentManager getFragmentManager(LifecycleOwner context) {
        FragmentManager fm = null;
        if (context instanceof Activity) {
            FragmentActivity activity = (FragmentActivity) context;
            fm = activity.getSupportFragmentManager();
        } else if (context instanceof Fragment) {
            Fragment fragment = (Fragment) context;
            fm = fragment.getChildFragmentManager();
        }
        return fm;
    }


    /**
     * 去缓存查找是否有已缓存的Fragment,主要是防止在同一界面，有多次请求权限需求
     * @param fragmentManager FragmentManager实例
     * @param tag fragment标签
     * @return PermissionFragment实例，如果缓存中没有则返回null
     */
    private static PermissionFragment findPermissionFragment(@Nullable FragmentManager fragmentManager , @Nullable String tag) {
        PermissionFragment permissionFragment = null;
        if (fragmentManager != null && !TextUtils.isEmpty(tag)){
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null){
                permissionFragment = (PermissionFragment)fragment;
            }
        }
        return  permissionFragment;
    }
}
