package com.example.bookmoth;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.utils.DatabaseManager;

public class BookMothApplication extends Application {
    private DatabaseManager databaseManager;

    @Override
    public void onCreate() {
        super.onCreate();

        databaseManager = new DatabaseManager(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(databaseManager);

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
