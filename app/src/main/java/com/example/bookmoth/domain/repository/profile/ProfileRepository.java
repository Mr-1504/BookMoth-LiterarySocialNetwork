package com.example.bookmoth.domain.repository.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.UsernameResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

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

    Call<Void> editProfile(
            Map<String, RequestBody> params,
            MultipartBody.Part avatar,
            MultipartBody.Part cover
    );
}
