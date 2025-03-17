package com.example.bookmoth.data.remote.profile;

import com.example.bookmoth.domain.model.profile.Profile;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ProfileApiService {
    @GET("api/profile/me")
    Call<Profile> getProfile();
}
