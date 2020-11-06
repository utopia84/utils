package com.runtime.permission;

import android.content.Context;
import android.os.Build;

import com.runtime.permission.annotation.NeedsPermission;
import com.runtime.permission.annotation.OnNeverAskAgain;
import com.runtime.permission.annotation.OnPermissionDenied;
import com.runtime.permission.annotation.OnShowRationale;
import com.runtime.permission.fragment.PermissionFragment;
import com.runtime.permission.utils.FragmentUtils;
import com.runtime.permission.utils.PermissionCallback;
import com.runtime.permission.utils.PermissionUtils;
import com.runtime.permission.utils.ReflectionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import androidx.lifecycle.LifecycleOwner;

@Aspect
public class Permissions {
    //execution(<@注解？> <修饰符?> <返回值类型> <类型声明?>.<方法名>(参数列表) <异常列表>？)
    @Around("execution(@com.runtime.permission.annotation.NeedsPermission * *(..)) && @annotation(needsPermission)")
    public void onProcessMethod(ProceedingJoinPoint joinPoint, NeedsPermission needsPermission) throws Throwable {
        LifecycleOwner lifecycleOwner = (LifecycleOwner) joinPoint.getThis();
        if (lifecycleOwner != null) {
            String[] permissions = needsPermission.value();
            int requestCode = needsPermission.requestCode();

            PermissionFragment permissionFragment = FragmentUtils.getPermissionFragment(lifecycleOwner,"PermissionFragment");
            // 使用PermissionFragment代理申请权限
            permissionFragment.request(permissions, requestCode, new PermissionCallback() {
                @Override
                public void granted(int requestCode) {//授权
                    try {
                        joinPoint.proceed();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void denied(Context context,int requestCode)  {//拒绝
                    ReflectionUtils.invoke(context, OnPermissionDenied.class, requestCode);
                }

                @Override
                public void neverAsk(Context context,int requestCode) {//不再提醒
                    ReflectionUtils.invoke(context, OnNeverAskAgain.class, requestCode);
                }

                @Override
                public void cancel(Context context,int requestCode) {//取消
                    ReflectionUtils.invoke(context, OnShowRationale.class, requestCode);
                }
            });
        }
    }


}
