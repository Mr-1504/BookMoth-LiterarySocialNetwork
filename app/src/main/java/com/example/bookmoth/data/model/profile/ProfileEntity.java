package com.example.bookmoth.data.model.profile;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * Lớp định nghĩa đối tượng hồ sơ người dùng.
 */
@Entity(tableName = "profile")
public class ProfileEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "profile_id")
    private String profileId;   // ID hồ sơ

    @ColumnInfo(name = "account_id")
    private String accountId;   // ID tài khoản liên kết

    @ColumnInfo(name = "first_name")
    private String firstName;   // Họ của người dùng

    @ColumnInfo(name = "last_name")
    private String lastName;    // Tên của người dùng

    @ColumnInfo(name = "username")
    private String username;    // Tên đăng nhập

    @ColumnInfo(name = "avatar")
    private String avatar;      // Ảnh đại diện

    @ColumnInfo(name = "cover_photo")
    private String coverPhoto;  // Ảnh bìa

    @ColumnInfo(name = "gender")
    private int gender;         // Giới tính (0: Nam, 1: Nữ, 2: Khác)

    @ColumnInfo(name = "identifier")
    private boolean identifier; // Xác định tài khoản cá nhân hay nhóm

    @ColumnInfo(name = "date_of_birth")
    private String dateOfBirth; // Ngày sinh (ISO Format)

    public ProfileEntity(@NonNull String profileId, String accountId, String firstName, String lastName,
                         String username, String avatar, String coverPhoto, int gender,
                         boolean identifier, String dateOfBirth) {
        this.profileId = profileId;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.avatar = avatar;
        this.coverPhoto = coverPhoto;
        this.gender = gender;
        this.identifier = identifier;
        this.dateOfBirth = dateOfBirth;
    }

    // Getter & Setter
    @NonNull
    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(@NonNull String profileId) {
        this.profileId = profileId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
