package com.example.bookmoth;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "FCM_CHANNEL",
                    "FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }
}
