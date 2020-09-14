package com.zjmy.epub.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
    /**
     * 解压指定文件到指定目录
     *
     * @param filePath 压缩包路径
     * @param savePath 解压后保存的路径
     * @param fileName 指定解压的文件名
     * @return 成功true
     */
    public static boolean zipSpecifiedFile(String filePath, String savePath, String fileName) {
        ZipFile zipFile = null;
        //String[] path = fileName.split("/");
        try {
            /*File file = new File(filePath);
            file.getParentFile();*/
            zipFile = new ZipFile(new File(filePath));

            ZipEntry zipEntry = zipFile.getEntry(fileName);
            //从zip包中读取给定文件名的内容
            byte[] bytes = getContent(zipFile, zipEntry);

            File file = new File(savePath + fileName);
            if (!file.exists()) {
                File parentPath = file.getParentFile();
                if (!parentPath.exists()) {
                    parentPath.mkdirs();
                }
                file.createNewFile();
            }
            //写入文件
            ZipUtil.write(file, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 从zip包中读取给定文件名的内容
     *
     * @param zipFile
     * @param zipEntry
     * @return
     * @throws IOException
     */
    public static byte[] getContent(final ZipFile zipFile,
                                    final ZipEntry zipEntry) throws IOException {
        InputStream inputStream = zipFile.getInputStream(zipEntry);
        byte[] buffer = new byte[1024];
        byte[] bytes = new byte[0];
        int length;
        while ((length = (inputStream.read(buffer))) != -1) {
            byte[] readBytes = new byte[length];
            System.arraycopy(buffer, 0, readBytes, 0, length);
            bytes = mergeArray(bytes, readBytes);
        }
        inputStream.close();
        return bytes;
    }

    /**
     * 写入单个文件
     *
     * @param file
     * @param bytes
     * @throws IOException
     */
    public static void write(final File file, final byte[] bytes)
            throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * 合并数组
     *
     * @param a
     * @return
     */
    public static byte[] mergeArray(byte[]... a) {
        // 合并完之后数组的总长度
        int index = 0;
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i].length;
        }
        byte[] result = new byte[sum];
        for (int i = 0; i < a.length; i++) {
            int lengthOne = a[i].length;
            if (lengthOne == 0) {
                continue;
            }
            // 拷贝数组
            System.arraycopy(a[i], 0, result, index, lengthOne);
            index = index + lengthOne;
        }
        return result;
    }
}
