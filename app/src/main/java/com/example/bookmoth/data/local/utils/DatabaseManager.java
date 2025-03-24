package com.example.bookmoth.data.local.utils;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.bookmoth.data.local.profile.ProfileDatabase;

public class DatabaseManager implements LifecycleObserver {
    private final ProfileDatabase database;

    public DatabaseManager(Application application) {
        this.database = ProfileDatabase.getInstance(application);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void closeDatabase() {
        if (database != null) {
            Log.i("DatabaseManager", "Closing database");
            database.close();
        }
    }
}

