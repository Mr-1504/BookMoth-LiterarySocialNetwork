package com.example.bookmoth.data.remote.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.UsernameResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

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

    /**
     * Lấy thông tin hồ sơ của người dùng theo id.
     *
     * @param profileId id của hồ sơ người dùng.
     * @return {@link Call} chứa đối tượng {@link Profile}, phản hồi từ API.
     */
    @GET("api/profile/{id}")
    Call<Profile> getProfileById(@Path("id") String profileId);

    /**
     * Kiểm tra xem username đã tồn tại hay chưa.
     *
     * @param username username cần kiểm tra.
     * @return {@link Call} chứa thông tin về việc username đã tồn tại hay chưa.
     */
    @GET("api/profile/exists/{username}")
    Call<UsernameResponse> checkUsername(@Path("username") String username);



    @Multipart
    @PATCH("api/profile/edit")
    Call<Profile> editProfile(
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part cover
    );
}
