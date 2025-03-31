package com.example.bookmoth.data.local.profile;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.bookmoth.data.model.profile.ProfileEntity;

/**
 * Interface định nghĩa các phương thức truy cập dữ liệu liên quan đến hồ sơ người dùng.
 */
@Dao
public interface ProfileDao {

    /**
     * Lưu thông tin hồ sơ người dùng.
     * @param profile Đối tượng chứa thông tin hồ sơ người dùng.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveProfile(ProfileEntity profile);

    /**
     * Lấy thông tin hồ sơ người dùng từ bộ nhớ cục bộ.
     * @return Đối tượng chứa thông tin hồ sơ người dùng.
     */
    @Query("Select * from profile limit 1")
    ProfileEntity getProfileLocal();

    /**
     * Xóa thông tin hồ sơ người dùng khỏi bộ nhớ cục bộ.
     */
    @Query("Delete from profile")
    void deleteProfileLocal();
}
