package com.example.bookmoth.domain.usecase.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.repository.profile.ProfileRepository;

import retrofit2.Call;

/**
 * Lớp chứa các use case liên quan đến hồ sơ người dùng (Profile).
 * Đóng vai trò trung gian giữa Repository và ViewModel/Controller.
 */
public class ProfileUseCase {
    private final ProfileRepository profileRepository;

    /**
     * Constructor khởi tạo `ProfileUseCase` với `ProfileRepository`.
     *
     * @param profileRepository Repository xử lý logic lấy dữ liệu hồ sơ người dùng.
     */
    public ProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Lấy thông tin hồ sơ của người dùng.
     *
     * @return Đối tượng Call chứa thông tin hồ sơ của người dùng.
     */
    public Call<Profile> getProfile() {
        return profileRepository.getProfile();
    }
}
