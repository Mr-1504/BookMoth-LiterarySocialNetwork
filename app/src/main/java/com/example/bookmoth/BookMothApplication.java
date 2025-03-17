package com.example.bookmoth;

import android.app.Application;

import com.example.bookmoth.core.utils.SecureStorage;

public class BookMothApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SecureStorage.init(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
