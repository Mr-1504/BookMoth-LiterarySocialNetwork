package com.example.bookmoth.data.local.profile;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.bookmoth.data.model.profile.ProfileDao;
import com.example.bookmoth.data.model.profile.ProfileEntity;

/**
 * Lớp cơ sở dữ liệu Room chứa thông tin hồ sơ người dùng.
 */
@Database(entities = {ProfileEntity.class}, version = 1)
public abstract class ProfileDatabase extends RoomDatabase {
    public abstract ProfileDao profileDao();

    private static ProfileDatabase instance;

    /**
     * Lấy đối tượng cơ sở dữ liệu Room.
     * @param context Context của ứng dụng.
     * @return Đối tượng cơ sở dữ liệu Room.
     */
    public static synchronized ProfileDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ProfileDatabase.class, "profile_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
