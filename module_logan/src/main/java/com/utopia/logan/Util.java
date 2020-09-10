package com.utopia.logan;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Util {

    public static boolean loadLibrary(String loadName, Class className) {
        boolean isLoad = false;
        try {
            ClassLoader classLoader = className.getClassLoader();
            Class runtime = Runtime.getRuntime().getClass();
            Class[] args = new Class[2];
            int version = android.os.Build.VERSION.SDK_INT;
            String functionName = "loadLibrary";
            if (version > 24) {
                args[0] = ClassLoader.class;
                args[1] = String.class;
                functionName = "loadLibrary0";
                Method loadMethod = runtime.getDeclaredMethod(functionName, args);
                loadMethod.setAccessible(true);
                loadMethod.invoke(Runtime.getRuntime(), classLoader, loadName);
            } else {
                args[0] = String.class;
                args[1] = ClassLoader.class;
                Method loadMethod = runtime.getDeclaredMethod(functionName, args);
                loadMethod.setAccessible(true);
                loadMethod.invoke(Runtime.getRuntime(), loadName, classLoader);
            }
            isLoad = true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return isLoad;
    }

    public static void ioClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            }
        }
    }

    public static byte[] readFile2BytesByMap(final File file) {
        if (file == null || !file.exists()) return null;
        FileChannel fc = null;
        try {
            fc = new RandomAccessFile(file, "r").getChannel();
            int size = (int) fc.size();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
            byte[] result = new byte[size];
            mbb.get(result, 0, size);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fc != null) {
                    fc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
