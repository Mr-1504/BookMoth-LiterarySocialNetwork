package com.example.bookmoth.core.utils;


import java.nio.charset.StandardCharsets;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * Lớp hỗ trợ tạo HMAC từ một chuỗi thông điệp và một khóa bí mật.
 */
public class HMacHelper {

    /**
     * Tính toán HMAC từ một chuỗi thông điệp và một khóa bí mật.
     *
     * @param key     Khóa bí mật.
     * @param message Chuỗi thông điệp.
     * @return Chuỗi HMAC.
     */
    public static String computeHmac(String key, String message) {
        try {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
            byte[] hashMessage = mac.doFinal(messageBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashMessage) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo HMAC: " + e.getMessage(), e);
        }
    }
}
