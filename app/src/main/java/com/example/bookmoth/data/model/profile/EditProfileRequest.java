package com.example.bookmoth.data.model.profile;

import okhttp3.MultipartBody;

public class EditProfileRequest {
    private String firstName;   // Họ của người dùng
    private String lastName;    // Tên của người dùng
    private String username;    // Tên đăng nhập
    private MultipartBody.Part avatar;      // Ảnh đại diện
    private MultipartBody.Part cover;
    private int gender;         // Giới tính (0: Nam, 1: Nữ, 2: Khác)
    private boolean identifier; // Xác định tài khoản cá nhân hay nhóm
    private String birth; // Ngày sinh
}
