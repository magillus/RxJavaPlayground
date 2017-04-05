package com.example.mat.rxjavaplayground.service;

import com.example.mat.rxjavaplayground.utils.ThreadLogging;

/**
 * Created on 12/13/16.
 */

public class StringConverters {
    public static String toUppercase(String s) {
        ThreadLogging.log("transform: To uppercase text");
        if (s != null) {
            return s.toUpperCase();
        }
        return s;
    }

    public static Integer countLetters(String value) {
        ThreadLogging.log("transform: Count letters");
        if (value != null) {
            return value.length();
        }
        return 0;
    }

    public static String toLowercase(String text) {
        ThreadLogging.log("transform: to lowercase text");
        if (text!=null) {
            return text.toLowerCase();
        }
        return text;
    }

    public static Integer countLetters(String text,String charList) {
        String filterText = text.replaceAll("[^"+charList+"]", "");
        return filterText.length();
    }
}
