package com.restaurant.util;

import java.security.MessageDigest;

public class PasswordUtils {

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(password.getBytes());

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
