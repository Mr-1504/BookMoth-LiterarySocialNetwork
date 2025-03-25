package com.example.bookmoth.data.remote.profile;

import com.example.bookmoth.domain.model.profile.Profile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

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

    @GET("api/profile")
    Call<Profile> getProfileById(@Query("id") String profileId);
}
