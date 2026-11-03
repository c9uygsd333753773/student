package com.sms.util;

import java.security.MessageDigest;

/**
 * MD5 加密工具（课程作业用途；生产环境建议使用 BCrypt 等强哈希 + 盐）。
 */
public class MD5Util {

    public static String md5(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(text.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
