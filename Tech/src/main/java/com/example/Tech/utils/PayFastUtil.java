package com.example.Tech.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

public class PayFastUtil {

    public static String generateSignature(Map<String, String> parameters, String passphrase) throws Exception {
        // Start building the parameter string
        StringBuilder paramString = new StringBuilder();

        // Append the parameters in alphabetical order, except for "signature"
        parameters.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getKey().equals("signature"))
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> paramString.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));

        // Append the passphrase if it exists (for PayFast accounts that require it)
        if (passphrase != null && !passphrase.isBlank()) {
            paramString.append("passphrase=").append(passphrase);
        } else {
            // Safely remove the last '&' if it exists
            int lastIndex = paramString.length() - 1;
            if (lastIndex >= 0 && paramString.charAt(lastIndex) == '&') {
                paramString.deleteCharAt(lastIndex);
            }
        }


        // Return MD5 hash of the concatenated string
        return md5(paramString.toString());
    }

    // MD5 hashing utility function
    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();

        // Convert the byte array to a hex string
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');  // pad single digit hex
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
