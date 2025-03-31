package com.example.bookmoth.data.remote.post;

import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
