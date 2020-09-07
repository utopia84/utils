package eink.yitoa.utils.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射相关类
 */
public final class ReflectUtils {
    private Method mMethod;
    private Class<?> mClazz;

    private ReflectUtils(){
        this.mClazz = null;
    }

    private ReflectUtils(Class<?> clazz){
        this.mClazz = clazz;
    }

    /**
     * 反射获取某个类
     * @param className 类全名（包含包名）
     * @return ReflectUtils
     */
    public static ReflectUtils reflect(String className) throws ReflectException {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
        return new ReflectUtils(clazz);
    }

    /**
     * 反射获取某个类里面的方法
     * @param methodName 方法全名
     * @param parameterTypes 参数类型
     * @return Method
     */
    public ReflectUtils method(String methodName, Class<?>... parameterTypes) throws ReflectException {
        if (mClazz != null) {
            try {
                mMethod = mClazz.getDeclaredMethod(methodName,parameterTypes);
                mMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new ReflectException(e);
            }
        }else{
            throw new ReflectException("ClassNotFoundException");
        }
        return this;
    }

    /**
     * 开始调用方法
     * @param receiver 调用此方法的object
     * @param args 方法参数
     */
    public Object invoke(Object receiver, Object... args) throws ReflectException {
        Object t = null;
        if (mMethod != null){
            try {
                t = mMethod.invoke(null,args);
            } catch (Exception e) {
                throw new ReflectException("方法调用错误！");
            }
        }else{
            throw new ReflectException("NoSuchMethodException");
        }
        return t;
    }
}
