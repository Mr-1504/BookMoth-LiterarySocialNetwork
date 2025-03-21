package com.example.bookmoth.domain.repository.profile;

import com.example.bookmoth.domain.model.profile.Profile;

import retrofit2.Call;

/**
 * Interface định nghĩa các phương thức liên quan đến hồ sơ người dùng.
 */
public interface ProfileRepository {

    /**
     * Lấy thông tin hồ sơ của người dùng.
     *
     * @return Đối tượng Call chứa thông tin hồ sơ của người dùng.
     */
    Call<Profile> getProfile();
}
