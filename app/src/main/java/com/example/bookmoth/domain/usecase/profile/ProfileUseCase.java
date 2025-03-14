package com.example.bookmoth.domain.usecase.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.repository.profile.ProfileRepository;

import retrofit2.Call;

public class ProfileUseCase {
    private final ProfileRepository profileRepository;

    public ProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Call<Profile> getProfile() {
        return profileRepository.getProfile();
    }
}
