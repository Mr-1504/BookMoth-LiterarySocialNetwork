package com.example.bookmoth.domain.repository.post;

import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Profile;

import java.util.List;

import retrofit2.Call;

public interface FlaskRepository {
    Call<ApiResponse<List<Book>>> getBooks();
    Call<ApiResponse<Book>> getBookById(int id);
    Call<ApiResponse<Profile>> getProfile(int id);
    Call<Api> getProfileAvata(int id);
}
