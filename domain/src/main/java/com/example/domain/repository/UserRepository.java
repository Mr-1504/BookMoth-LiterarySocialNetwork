package com.example.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.domain.entity.User;

import java.util.List;

public interface UserRepository {
    LiveData<List<User>> getUsers();
}
