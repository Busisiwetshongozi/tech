package com.example.Tech.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public class PayFastUtil {

    public static String generateSignature(Map<String, String> parameters, String passphrase) throws Exception {
        StringBuilder paramString = new StringBuilder();

        parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getKey().equals("signature"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));

        if (passphrase != null && !passphrase.isEmpty()) {
            paramString.append("passphrase=").append(passphrase);
        } else if (paramString.length() > 0) {
            paramString.deleteCharAt(paramString.length() - 1);
        }

        return md5(paramString.toString());
    }

    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
