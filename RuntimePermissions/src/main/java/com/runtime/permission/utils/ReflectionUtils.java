package com.runtime.permission.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;

public class ReflectionUtils {
    /**
     * 通过反射调用被指定Annotation修饰的方法，且方法参数必须为1
     *
     * @param obj            方法所在的对象
     * @param annotationType 指定注解
     * @param requestCode    方法参数
     */
    public static void invoke(@NonNull Object obj, Class<? extends Annotation> annotationType, int requestCode) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationType)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                method.setAccessible(true);

                try {
                    if (parameterTypes.length == 1 && parameterTypes[0] == Integer.class) {
                        method.invoke(obj, requestCode);
                    } else if (parameterTypes.length == 0) {
                        method.invoke(obj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
