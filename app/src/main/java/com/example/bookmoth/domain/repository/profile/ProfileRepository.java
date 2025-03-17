package com.example.bookmoth.domain.repository.profile;

import com.example.bookmoth.domain.model.profile.Profile;

import retrofit2.Call;

public interface ProfileRepository {
    Call<Profile> getProfile();
}
