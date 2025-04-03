package com.example.bookmoth.data.remote.post;

import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Profile;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FlaskApiService {
    @GET("pinbooks")  // Đường dẫn API lấy danh sách sách
    Call<ApiResponse<List<Book>>> getBooks();

    @GET("books/{id}")
    Call<ApiResponse<Book>> getBookById(@Path("id") int id);

    @GET("/profile/{id}")
    Call<ApiResponse<Profile>> getProfile(@Path("id") int id);

    @GET("/profile_avata/{id}")
    Call<Api> getProfileAvata(@Path("id") int id);

    @GET("/idfollowers/{id}")
    Call<Api> getFollowers(@Path("id") int id);

    @Multipart
    @POST("/check-blood/{id}")
    Call<Api> checkBlood(@Path("id") int id, @Part MultipartBody.Part body);

    @Multipart
    @POST("/process-image/{id}")
    Call<ResponseBody> processImage(@Path("id") int id, @Part MultipartBody.Part body);
}
