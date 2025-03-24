package com.example.bookmoth.domain.model.profile;

import com.example.bookmoth.data.model.profile.ProfileEntity;

import java.io.Serializable;

/**
 * Class đại diện cho hồ sơ người dùng.
 * Lưu trữ thông tin cá nhân của người dùng, bao gồm tên, tài khoản, giới tính, ngày sinh, ảnh đại diện, v.v.
 */
public class Profile implements Serializable {
    private String profileId;   // ID hồ sơ
    private String accountId;   // ID tài khoản liên kết
    private String firstName;   // Họ của người dùng
    private String lastName;    // Tên của người dùng
    private String username;    // Tên đăng nhập
    private String avatar;      // Ảnh đại diện
    private String coverphoto;  // Ảnh bìa
    private int gender;         // Giới tính (0: Nam, 1: Nữ, 2: Khác)
    private boolean identifier; // Xác định tài khoản cá nhân hay nhóm
    private String birth; // Ngày sinh


    public Profile(ProfileEntity entity) {
        this.profileId = entity.getProfileId();
        this.accountId = entity.getAccountId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.username = entity.getUsername();
        this.avatar = entity.getAvatar();
        this.coverphoto = entity.getCoverPhoto();
        this.gender = entity.getGender();
        this.identifier = entity.isIdentifier();
        this.birth = entity.getDateOfBirth();
    }

    /**
     * Lấy ngày sinh của người dùng.
     *
     * @return Ngày sinh dưới dạng chuỗi (yyyy-MM-dd)
     */
    public String getBirth() {
        return birth;
    }

    /**
     * Thiết lập ngày sinh cho người dùng.
     *
     * @param birth Ngày sinh (yyyy-MM-dd)
     */
    public void setBirth(String birth) {
        this.birth = birth;
    }

    /**
     * Lấy ảnh đại diện của người dùng.
     *
     * @return Đường dẫn hoặc URL ảnh đại diện.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Cập nhật ảnh đại diện của người dùng.
     *
     * @param avatar Đường dẫn hoặc URL ảnh đại diện mới.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Lấy ảnh bìa của người dùng.
     *
     * @return Đường dẫn hoặc URL ảnh bìa.
     */
    public String getCoverphoto() {
        return coverphoto;
    }

    /**
     * Cập nhật ảnh bìa của người dùng.
     *
     * @param coverphoto Đường dẫn hoặc URL ảnh bìa mới.
     */
    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }

    /**
     * Lấy họ của người dùng.
     *
     * @return Họ của người dùng.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Cập nhật họ của người dùng.
     *
     * @param firstName Họ mới.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Lấy giới tính của người dùng.
     *
     * @return Giá trị số đại diện cho giới tính (0: Nam, 1: Nữ, 2: Khác).
     */
    public int getGender() {
        return gender;
    }

    /**
     * Cập nhật giới tính của người dùng.
     *
     * @param gender Giá trị số đại diện cho giới tính (0: Nam, 1: Nữ, 2: Khác).
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Kiểm tra xem tài khoản đã được xác minh hay chưa.
     *
     * @return {@code true} nếu tài khoản đã được xác minh, {@code false} nếu chưa.
     */
    public boolean isIdentifier() {
        return identifier;
    }

    /**
     * Cập nhật trạng thái xác minh tài khoản.
     *
     * @param identifier {@code true} nếu tài khoản đã được xác minh, {@code false} nếu chưa.
     */
    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    /**
     * Lấy tên của người dùng.
     *
     * @return Tên của người dùng.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Cập nhật tên của người dùng.
     *
     * @param lastName Tên mới.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Lấy ID hồ sơ của người dùng.
     *
     * @return ID hồ sơ.
     */
    public String getProfileId() {
        return profileId;
    }

    /**
     * Cập nhật ID hồ sơ của người dùng.
     *
     * @param profileId ID hồ sơ mới.
     */
    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    /**
     * Lấy tên đăng nhập của người dùng.
     *
     * @return Tên đăng nhập.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Cập nhật tên đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập mới.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Lấy ID tài khoản của người dùng.
     *
     * @return ID tài khoản.
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Cập nhật ID tài khoản của người dùng.
     *
     * @param accountId ID tài khoản mới.
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Trả về chuỗi mô tả đối tượng Profile.
     *
     * @return Chuỗi chứa thông tin hồ sơ người dùng.
     */
    @Override
    public String toString() {
        return "Profile{" +
                "profileId='" + profileId + '\'' +
                ", accountId='" + accountId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", coverPhoto='" + coverphoto + '\'' +
                ", gender=" + gender +
                ", identifier=" + identifier +
                ", dateOfBirth='" + birth + '\'' +
                '}';
    }
}
