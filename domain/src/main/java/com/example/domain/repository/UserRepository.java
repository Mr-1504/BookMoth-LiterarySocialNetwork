package com.example.domain.repository;


import com.example.domain.model.User;

import java.util.List;

import retrofit2.Callback;


public interface UserRepository {
    void getUsers(Callback<List<User>> callback);
}
