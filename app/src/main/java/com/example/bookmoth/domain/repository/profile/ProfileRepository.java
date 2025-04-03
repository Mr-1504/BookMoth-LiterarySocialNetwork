package com.example.bookmoth.domain.repository.profile;

import com.example.bookmoth.domain.model.profile.FollowResponse;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.ProfileResponse;
import com.example.bookmoth.domain.model.profile.UsernameResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

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

    /**
     * Lấy thông tin hồ sơ của người dùng theo id.
     *
     * @param profileId id của hồ sơ người dùng.
     * @return Đối tượng Call chứa thông tin hồ sơ của người dùng.
     */
    Call<Profile> getProfileById(String profileId);

    /**
     * Kiểm tra xem username đã tồn tại hay chưa.
     *
     * @param username username cần kiểm tra.
     * @return Đối tượng Call chứa thông tin về việc username đã tồn tại hay chưa.
     */
    Call<UsernameResponse> checkUsername(String username);

    /**
     * Chỉnh sửa thông tin hồ sơ người dùng.
     *
     * @param params thông tin hồ sơ cần chỉnh sửa.
     * @param avatar ảnh đại diện mới.
     * @param cover  ảnh bìa mới.
     * @return Đối tượng Call chứa thông tin hồ sơ người dùng sau khi chỉnh sửa.
     */
    Call<Profile> editProfile(
            Map<String, RequestBody> params,
            MultipartBody.Part avatar,
            MultipartBody.Part cover
    );

    /**
     * Tìm kiếm hồ sơ người dùng theo tên.
     *
     * @param searchString tên cần tìm kiếm.
     * @return Đối tượng Call chứa danh sách hồ sơ người dùng tìm được.
     */
    Call<List<ProfileResponse>> searchProfile(String searchString);

    /**
     * Follow người dùng.
     *
     * @param profileId id của người dùng cần follow
     * @return Đối tượng Call chứa thông tin hồ sơ người dùng sau khi follow.
     */
    Call<Void> follow(String profileId);

    /**
     * Unfollow người dùng.
     *
     * @param profileId id của người dùng cần unfollow
     * @return Đối tượng Call chứa thông tin hồ sơ người dùng sau khi unfollow.
     */
    Call<Void> unfollow(String profileId);

    /**
     * Kiểm tra xem người dùng đã follow người dùng khác chưa.
     *
     * @param profileId id của người dùng cần kiểm tra.
     * @return Đối tượng Call chứa thông tin về việc người dùng đã follow người dùng khác chưa.
     */
    Call<FollowResponse> isFollowing(String profileId);
}
