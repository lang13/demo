package com.eem.demo.util;

import org.springframework.util.DigestUtils;

/**
 * @author Administrator
 */
public class Md5Util {

    private static final String slat = "lang丶GDUT";

    public static String getMd5(String msg){
        String base = msg + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
