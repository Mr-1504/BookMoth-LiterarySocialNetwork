package com.example.bookmoth.data.remote.profile;

import com.example.bookmoth.domain.model.profile.Profile;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface định nghĩa API liên quan đến hồ sơ người dùng.
 */
public interface ProfileApiService {

    /**
     * Lấy thông tin hồ sơ của người dùng hiện tại.
     *
     * @return {@link Call} chứa đối tượng {@link Profile}, phản hồi từ API.
     */
    @GET("api/profile/me")
    Call<Profile> getProfile();
}
