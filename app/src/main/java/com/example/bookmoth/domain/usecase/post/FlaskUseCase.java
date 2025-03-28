package com.example.bookmoth.domain.usecase.post;

import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.repository.post.FlaskRepository;
import com.example.bookmoth.domain.model.post.Book;

import java.util.List;

import retrofit2.Call;

public class FlaskUseCase {
    FlaskRepository flaskRepository;
    public FlaskUseCase(FlaskRepository flaskRepository) {
        if (flaskRepository == null) {
            throw new IllegalStateException("FlaskRepository is null");
        }
        this.flaskRepository = flaskRepository;
    }

    public Call<ApiResponse<List<Book>>> getBooks() {
        return flaskRepository.getBooks();
    }
    public Call<ApiResponse<Book>> getBookById(int id) {
        return flaskRepository.getBookById(id);
    }
    public Call<ApiResponse<Profile>> getProfile(int id) {
        return flaskRepository.getProfile(id);
    }
    public Call<Api> getProfileAvata(int id) {
        return flaskRepository.getProfileAvata(id);
    }

    public Call<Api> getFollowers(int id) {
        return flaskRepository.getFollowers(id);
    }


}
