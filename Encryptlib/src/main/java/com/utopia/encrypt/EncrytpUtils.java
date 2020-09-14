package com.utopia.encrypt;


public class EncrytpUtils {
    public static boolean checkEncry(String str_tmp1, String str_tmp2) {
        if (str_tmp1 == null || str_tmp2 == null){
            return false;
        }

        return str_tmp1.compareToIgnoreCase(str_tmp2) == 0;
    }
}
