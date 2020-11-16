package com.zjmy.sdk.oss.common.utils;

import android.os.Build;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TODO
 *
 * @author free_
 * @version 1.0
 * @date 2020/11/16 13:17
 */
public class FileUtils {
    private final static int BUFFER_SIZE = 2048;

    public static boolean writeFileFromIS(final String filePath, final InputStream is) throws IOException{
        return writeFileFromIS(new File(filePath), is );
    }

    public static boolean writeFileFromIS(final File file, final InputStream is) throws IOException{
        if (!createOrExistsFile(file) || is == null) return false;
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, false));
            byte[] data = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(data, 0, BUFFER_SIZE)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } finally {
            IOUtils.safeClose(os);
        }
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }
}
