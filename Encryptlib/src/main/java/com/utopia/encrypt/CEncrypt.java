package com.utopia.encrypt;


public class CEncrypt {

    static{
        System.loadLibrary("JNIEncrypt");
    }

    public native static String StringMD5(String str);

    public native static String FileMD5(String filePath, boolean minSize);

    public static native String base64Encode(String str);

    public static native String base64Decode(String str);

}
