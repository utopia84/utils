package eink.yitoa.utils.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射相关类
 */
public final class ReflectUtils {
    private Method mMethod;
    private Class<?> mClazz;
    private Object mInstance;

    private ReflectUtils(){}

    private ReflectUtils(Class<?> clazz){
        this.mClazz = clazz;
    }

    /**
     * 反射获取某个类
     * @param className 类全名（包含包名）
     * @return ReflectUtils
     */
    public static ReflectUtils reflect(String className) throws ReflectException {
        Class<?> clazz ;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ReflectException(e);
        }
        return new ReflectUtils(clazz);
    }

    /**
     * 直接通过class反射
     */
    public static ReflectUtils reflect(final Class<?> clazz){
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
     * 需要实例化后调用反射方法的，需要在invoke之前调用，否则默认以静态方法方式调用method
     * @return ReflectUtils 实例
     */
    public ReflectUtils newInstance() throws ReflectException {
        if (mClazz != null) {
            try {
                mInstance = mClazz.newInstance();
            } catch (Exception e) {
                throw new ReflectException(e);
            }
        }else{
            throw new ReflectException("ClassNotFoundException");
        }
        return this;
    }

    /**
     * 开始调用方法
     * @param args 方法参数
     */
    public Object invoke(Object... args) throws ReflectException {
        Object t ;
        if (mMethod != null){
            try {
                t = mMethod.invoke(mInstance,args);
            } catch (Exception e) {
                throw new ReflectException("方法调用错误！");
            }
        }else{
            throw new ReflectException("NoSuchMethodException");
        }
        return t;
    }
}
