package com.zjmy.sdk.oss.common.utils;

import java.io.File;

public class FilePathUtils {

    /**
     * 获取文件扩展名
     * @param filename 文件名或文件路径
     * @return 文件扩展名
     */
    public static String getSuffix(String filename) {
        String suffix = "";

        int index = filename.lastIndexOf(".");

        if (index != -1) {
            suffix = filename.substring(index + 1);
        }

        return suffix;
    }

    public static String getSuffix(String filename , String defaultSuffix) {

        int index = filename.lastIndexOf(".");

        if (index != -1) {
            defaultSuffix = "." + filename.substring(index + 1);
        }

        if (defaultSuffix.contains("?")){
            return defaultSuffix.split("\\?")[0];
        }else {
            return defaultSuffix;
        }
    }

    public static String getSuffix(File file) {
        return getSuffix(file.getName());
    }
}
