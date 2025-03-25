package com.example.bookmoth.domain.repository.profile;

import com.example.bookmoth.domain.model.profile.Profile;

public interface LocalProfileRepository {

    /**
     * Lưu thông tin hồ sơ người dùng.
     * @param profile Đối tượng chứa thông tin hồ sơ người dùng.
     */
    void saveProfile(Profile profile);

    /**
     * Lấy thông tin hồ sơ người dùng từ bộ nhớ cục bộ.
     * @return Đối tượng chứa thông tin hồ sơ người dùng.
     */
    Profile getProfileLocal();

    /**
     * Xóa thông tin hồ sơ người dùng khỏi bộ nhớ cục bộ.
     */
    void deleteProfileLocal();

    /**
     * Kiểm tra xem hồ sơ người dùng đã tồn tại hay chưa.
     * @return True nếu đã tồn tại, False nếu chưa tồn tại.
     */
    boolean isProfileExist();
}
