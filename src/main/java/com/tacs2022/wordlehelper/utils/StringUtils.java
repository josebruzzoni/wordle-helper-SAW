package com.tacs2022.wordlehelper.utils;

public class StringUtils {
    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }

        String strLowerCase = str.toLowerCase();

        return strLowerCase.substring(0, 1).toUpperCase() + strLowerCase.substring(1);
    }
}
