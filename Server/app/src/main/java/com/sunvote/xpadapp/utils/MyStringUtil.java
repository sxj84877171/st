package com.sunvote.xpadapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lihuimin on 2017/7/26.
 */

public class MyStringUtil {

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
}
