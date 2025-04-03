package com.example.bookmoth.domain.repository.post;

import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Profile;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public interface FlaskRepository {
    Call<ApiResponse<List<Book>>> getBooks();
    Call<ApiResponse<Book>> getBookById(int id);
    Call<ApiResponse<Profile>> getProfile(int id);
    Call<Api> getProfileAvata(int id);
    Call<Api> getFollowers(int id);
    Call<Api> checkBlood(int id, MultipartBody.Part body);
    Call<ResponseBody> processImage(int id, MultipartBody.Part body);
}
