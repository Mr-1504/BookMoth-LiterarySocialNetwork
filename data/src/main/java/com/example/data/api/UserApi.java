package com.example.data.api;


import com.example.domain.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {
    @GET("users")
    Call<List<User>> getUsers();
}
