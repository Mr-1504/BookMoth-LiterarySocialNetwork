package com.example.bookmoth.data.model.profile;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Lớp định nghĩa đối tượng hồ sơ người dùng.
 */
@Entity(tableName = "profile")
public class ProfileEntity {
    @PrimaryKey
    public String profileId;   // ID hồ sơ
    public String accountId;   // ID tài khoản liên kết
    public String firstName;   // Họ của người dùng
    public String lastName;    // Tên của người dùng
    public String username;    // Tên đăng nhập
    public String avatar;      // Ảnh đại diện
    public String coverPhoto;  // Ảnh bìa
    public int gender;         // Giới tính (0: Nam, 1: Nữ, 2: Khác)
    public boolean identifier; // Xác định tài khoản cá nhân hay nhóm
    public String dateOfBirth; // Ngày sinh
}
