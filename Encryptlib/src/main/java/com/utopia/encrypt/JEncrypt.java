package com.utopia.encrypt;

import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class JEncrypt {
    public static String javaBase64Encode(String str)
    {
        return android.util.Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
    }


    public static String javaBase64Decode(String str)
    {
        return new String(android.util.Base64.decode(str.getBytes(), Base64.DEFAULT));
    }

    public static String StringMD5(String info) {
        if (info == null || info.equals("")) {
            return "112233";
        }

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes(StandardCharsets.UTF_8));
            byte[] encryption = md5.digest();

            StringBuilder strBuf = new StringBuilder();
            for (byte b : encryption) {
                if (Integer.toHexString(0xff & b).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & b));
                } else {
                    strBuf.append(Integer.toHexString(0xff & b));
                }
            }
            return strBuf.toString();
        } catch (Exception e) {
            return "112233";
        }
    }

    public static String FileMD5(String filePath, boolean minSize) {
        int sum = 0;
        byte[] buffer = new byte[1024];
        int len;

        MessageDigest mMDigest;
        FileInputStream Input;
        File file = new File(filePath);
        if (!file.exists())
            return "12345";//随便输入个md5
        try {
            mMDigest = MessageDigest.getInstance("MD5");
            mMDigest.reset();
            Input = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(Input);
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                mMDigest.update(buffer, 0, len);
                if (++sum > 5 && minSize) {
                    break;
                }
            }
            Input.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger mBInteger = new BigInteger(1, mMDigest.digest());
        return mBInteger.toString(16);

    }
}
