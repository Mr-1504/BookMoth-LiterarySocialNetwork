package com.example.bookmoth.core.utils;

import androidx.security.crypto.MasterKeys;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;

public class SecureStorage {
    private static final String PREF_NAME = "secure_prefs";
    private static SharedPreferences sharedPreferences;

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

    public static void saveToken(String key, String token) {
        if (sharedPreferences != null) {
            sharedPreferences.edit().putString(key, token).apply();
        } else {
            throw new IllegalStateException("SecureStorage chưa được khởi tạo. Gọi init(context) trước.");
        }
    }

    public static String getToken(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, null);
        } else {
            throw new IllegalStateException("SecureStorage chưa được khởi tạo. Gọi init(context) trước.");
        }
    }

    public static void clearToken() {
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }
}
