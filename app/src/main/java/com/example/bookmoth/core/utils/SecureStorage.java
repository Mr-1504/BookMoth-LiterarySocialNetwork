package com.example.bookmoth.core.utils;

import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;


/**
 * Lớp hỗ trợ lưu trữ an toàn bằng cách sử dụng {@link EncryptedSharedPreferences}.
 * Dữ liệu được mã hóa với chuẩn AES256 để bảo vệ thông tin nhạy cảm như token.
 */
public class SecureStorage {
    private static final String PREF_NAME = "secure_prefs";
    private static SharedPreferences sharedPreferences;


    /**
     * Khởi tạo bộ lưu trữ an toàn.
     *
     * @param context Context của ứng dụng.
     * @throws Exception Nếu xảy ra lỗi trong quá trình tạo khóa mã hóa.
     */
    public static void init(Context context) throws Exception {
        String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

        sharedPreferences = EncryptedSharedPreferences.create(
                PREF_NAME,
                masterKey,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }


    /**
     * Lưu token vào bộ nhớ an toàn.
     *
     * @param key   Khóa đại diện cho giá trị cần lưu.
     * @param token Giá trị token cần lưu trữ.
     * @throws IllegalStateException Nếu chưa gọi {@link #init(Context)} trước khi lưu dữ liệu.
     */
    public static void saveToken(String key, String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, token).apply();
        } else {
            throw new IllegalStateException("SecureStorage chưa được khởi tạo. Gọi init(context) trước.");
        }
    }


    /**
     * Lấy token từ bộ nhớ an toàn.
     *
     * @param key Khóa đại diện cho giá trị cần lấy.
     * @return Token đã lưu, hoặc {@code null} nếu không có dữ liệu.
     * @throws IllegalStateException Nếu chưa gọi {@link #init(Context)} trước khi truy xuất dữ liệu.
     */
    public static String getToken(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, null);
        } else {
            throw new IllegalStateException("SecureStorage chưa được khởi tạo. Gọi init(context) trước.");
        }
    }


    /**
     * Xóa tất cả dữ liệu đã lưu trong bộ nhớ an toàn.
     */
    public static void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }
}
