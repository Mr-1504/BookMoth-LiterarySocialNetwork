package com.example.bookmoth.data.repository.post;

import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.remote.post.FlaskApiService;
import com.example.bookmoth.data.remote.utils.RetrofitClient;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Profile;
import com.example.bookmoth.domain.repository.post.FlaskRepository;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class FlaskRepositoryImpl implements FlaskRepository {
    FlaskApiService flaskApiService;

    public FlaskRepositoryImpl() {
        flaskApiService = RetrofitClient.getFlaskRetrofit().create(FlaskApiService.class);
    }

    @Override
    public Call<ApiResponse<List<Book>>> getBooks() {
        return flaskApiService.getBooks();
    }

    @Override
    public Call<ApiResponse<Book>> getBookById(int id) {
        return flaskApiService.getBookById(id);
    }

    @Override
    public Call<ApiResponse<Profile>> getProfile(int id) {
        return flaskApiService.getProfile(id);
    }

    @Override
    public Call<Api> getProfileAvata(int id) {
        return flaskApiService.getProfileAvata(id);
    }

    @Override
    public Call<Api> getFollowers(int id) {
        return flaskApiService.getFollowers(id);
    }

    @Override
    public Call<Api> checkBlood(int id, MultipartBody.Part body) {
        return flaskApiService.checkBlood(id, body);
    }

    @Override
    public Call<ResponseBody> processImage(int id, MultipartBody.Part body) {
        return flaskApiService.processImage(id, body);
    }
}
