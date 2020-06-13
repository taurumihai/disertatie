package com.tauru.shop.utilitare;

public class StringUtils {


    public static boolean isNullOrEmpty(String str) {

        if (str == null || str.isEmpty()) {

            return true;
        }

        return false;
    }
}
