package com.example.bookmoth.data.remote.profile;

import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.ProfileResponse;
import com.example.bookmoth.domain.model.profile.UsernameResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

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


    /**
     * Chỉnh sửa thông tin hồ sơ người dùng.
     *
     * @param params thông tin hồ sơ cần chỉnh sửa.
     * @param avatar ảnh đại diện mới.
     * @param cover  ảnh bìa mới.
     * @return {@link Call} chứa đối tượng {@link Profile}, phản hồi từ API.
     */
    @Multipart
    @PATCH("api/profile/edit")
    Call<Profile> editProfile(
            @PartMap Map<String, RequestBody> params,
            @Part MultipartBody.Part avatar,
            @Part MultipartBody.Part cover
    );

    /**
     * Tìm kiếm hồ sơ người dùng theo tên hoặc username.
     *
     * @param searchString chuỗi tìm kiếm.
     * @return {@link Call} chứa danh sách các hồ sơ người dùng phản hồi từ API.
     */
    @GET("api/profile/search")
    Call<List<ProfileResponse>> searchProfile(@Query("search") String searchString);

    /**
     * Follow người dùng.
     *
     * @param id id của người dùng cần follow.
     * @return {@link Call} chứa thông tin về việc follow người dùng.
     */
    @POST("api/profile/follow")
    Call<Void> follow(@Body String id);

    /**
     * Unfollow người dùng.
     *
     * @param id id của người dùng cần unfollow.
     * @return {@link Call} chứa thông tin về việc unfollow người dùng.
     */
    @DELETE("api/profile/follow/{followingId}")
    Call<Void> unfollow(@Path("followingId") String id);

}
