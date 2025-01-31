package com.example.domain.usecase;

import com.example.domain.model.User;
import com.example.domain.repository.UserRepository;

import java.util.List;

import retrofit2.Callback;

public class GetUsersUseCase {
    private final UserRepository userRepository;

    public GetUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(Callback<List<User>> callback){
        this.userRepository.getUsers(callback);
    }
}
