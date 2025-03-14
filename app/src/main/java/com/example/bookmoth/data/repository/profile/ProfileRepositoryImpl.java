package com.example.bookmoth.data.repository.profile;

import com.example.bookmoth.data.remote.profile.ProfileApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.repository.profile.ProfileRepository;

import retrofit2.Call;

public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileApiService profileApiService;

    public ProfileRepositoryImpl() {
        this.profileApiService = RetrofitClient.getInstance().create(ProfileApiService.class);
    }

    @Override
    public Call<Profile> getProfile() {
        return profileApiService.getProfile();
    }
}
