package com.example.bookmoth.data.repository.profile;

import com.example.bookmoth.data.model.profile.FollowRequest;
import com.example.bookmoth.data.remote.profile.ProfileApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.model.profile.FollowResponse;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.ProfileResponse;
import com.example.bookmoth.domain.model.profile.UsernameResponse;
import com.example.bookmoth.domain.repository.profile.ProfileRepository;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Class triển khai {@code ProfileRepository} để lấy thông tin hồ sơ người dùng.
 */
public class ProfileRepositoryImpl implements ProfileRepository {
    private final ProfileApiService profileApiService;

    /**
     * Khởi tạo repository và thiết lập {@code ProfileApiService} sử dụng Retrofit.
     */
    public ProfileRepositoryImpl() {
        this.profileApiService = RetrofitClient.getAspServerRetrofit().create(ProfileApiService.class);
    }

    /**
     * Lấy thông tin hồ sơ người dùng từ API.
     *
     * @return {@code Call<Profile>} phản hồi chứa thông tin hồ sơ người dùng.
     */
    @Override
    public Call<Profile> getProfile() {
        return profileApiService.getProfile();
    }

    /**
     * Lấy thông tin hồ sơ người dùng theo id từ API.
     *
     * @param profileId id của hồ sơ người dùng.
     * @return {@code Call<Profile>} phản hồi chứa thông tin hồ sơ người dùng.
     */
    @Override
    public Call<Profile> getProfileById(String profileId) {
        return profileApiService.getProfileById(profileId);
    }

    /**
     * Kiểm tra xem username đã tồn tại hay chưa.
     *
     * @param username username cần kiểm tra.
     * @return {@code Call<UsernameResponse>} phản hồi chứa thông tin về việc username đã tồn tại hay chưa.
     */
    @Override
    public Call<UsernameResponse> checkUsername(String username) {
        return profileApiService.checkUsername(username);
    }

    /**
     * Chỉnh sửa thông tin hồ sơ người dùng.
     *
     * @param params thông tin hồ sơ cần chỉnh sửa.
     * @param avatar ảnh đại diện mới.
     * @param cover  ảnh bìa mới.
     * @return {@code Call<Profile>} phản hồi chứa thông tin hồ sơ người dùng sau khi chỉnh sửa.
     */
    @Override
    public Call<Profile> editProfile(Map<String, RequestBody> params,  MultipartBody.Part avatar, MultipartBody.Part cover) {
        return profileApiService.editProfile(params, avatar, cover);
    }

    /**
     * Tìm kiếm hồ sơ người dùng theo tên.
     *
     * @param searchString tên cần tìm kiếm.
     * @return {@code Call<List<ProfileResponse>} phản hồi chứa danh sách hồ sơ người dùng tìm được.
     */
    @Override
    public Call<List<ProfileResponse>> searchProfile(String searchString) {
        return profileApiService.searchProfile(searchString);
    }

    /**
     * Follow người dùng.
     *
     * @param profileId id của người dùng cần follow
     * @return {@code Call<Void>} phản hồi chứa thông tin hồ sơ người dùng sau khi follow.
     */
    @Override
    public Call<Void> follow(String profileId) {
        return profileApiService.follow(new FollowRequest(profileId));
    }

    /**
     * Unfollow người dùng.
     *
     * @param profileId id của người dùng cần unfollow
     * @return {@code Call<Void>} phản hồi chứa thông tin hồ sơ người dùng sau khi unfollow.
     */
    @Override
    public Call<Void> unfollow(String profileId) {
        return profileApiService.unfollow(profileId);
    }

    /**
     * Kiểm tra xem người dùng đã follow người dùng khác chưa.
     *
     * @param profileId id của người dùng cần kiểm tra.
     * @return {@code Call<Boolean>} phản hồi chứa thông tin về việc follow người dùng.
     */
    @Override
    public Call<FollowResponse> isFollowing(String profileId) {
        return profileApiService.isFollowing(profileId);
    }
}
