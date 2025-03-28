package com.example.bookmoth.domain.usecase.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.UsernameResponse;
import com.example.bookmoth.domain.repository.profile.LocalProfileRepository;
import com.example.bookmoth.domain.repository.profile.ProfileRepository;

import retrofit2.Call;

/**
 * Lớp chứa các use case liên quan đến hồ sơ người dùng (Profile).
 * Đóng vai trò trung gian giữa Repository và ViewModel/Controller.
 */
public class ProfileUseCase {
    private final LocalProfileRepository localRepo;
    private final ProfileRepository remoteRepo;

    /**
     * Constructor khởi tạo `ProfileUseCase` với `ProfileRepository`.
     *
     * @param remoteRepo Repository xử lý logic lấy dữ liệu hồ sơ người dùng.
     */
    public ProfileUseCase(LocalProfileRepository localRepo, ProfileRepository remoteRepo) {
        this.localRepo = localRepo;
        this.remoteRepo = remoteRepo;
    }

    /**
     * Lấy thông tin hồ sơ của người dùng.
     *
     * @return Đối tượng Call chứa thông tin hồ sơ của người dùng.
     */
    public Call<Profile> getProfile() {
        return remoteRepo.getProfile();
    }

    /**
     * Lưu thông tin hồ sơ người dùng.
     * @param profile Đối tượng chứa thông tin hồ sơ người dùng.
     */
    public void saveProfile(Profile profile) {
        localRepo.saveProfile(profile);
    }

    /**
     * Lấy thông tin hồ sơ người dùng từ bộ nhớ cục bộ.
     * @return Đối tượng chứa thông tin hồ sơ người dùng.
     */
    public Profile getProfileLocal() {
        return localRepo.getProfileLocal();
    }

    /**
     * Xóa thông tin hồ sơ người dùng khỏi bộ nhớ cục bộ.
     */
    public void deleteProfileLocal() {
        localRepo.deleteProfileLocal();
    }

    /**
     * Kiểm tra xem hồ sơ người dùng đã tồn tại hay chưa.
     * @return True nếu đã tồn tại, False nếu chưa tồn tại.
     */
    public boolean isProfileExist() {
        return localRepo.isProfileExist();
    }

    /**
     * Lấy thông tin hồ sơ của người dùng theo id.
     *
     * @param profileId id của hồ sơ người dùng.
     * @return Đối tượng Call chứa thông tin hồ sơ của người dùng.
     */
    public Call<Profile> getProfileById(String profileId) {
        return remoteRepo.getProfileById(profileId);
    }

    /**
     * Kiểm tra xem username đã tồn tại hay chưa.
     *
     * @param username username cần kiểm tra.
     * @return Đối tượng Call chứa thông tin về việc username đã tồn tại hay chưa.
     */
    public Call<UsernameResponse> checkUsername(String username) {
        return remoteRepo.checkUsername(username);
    }
}
