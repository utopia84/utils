package eink.yitoa.utils.common;

/**
 * 反射异常封装
 */
public class ReflectException extends Exception {

    public ReflectException(String message) {
        super(message);
    }

    public ReflectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectException(Throwable cause) {
        super(cause);
    }
}
