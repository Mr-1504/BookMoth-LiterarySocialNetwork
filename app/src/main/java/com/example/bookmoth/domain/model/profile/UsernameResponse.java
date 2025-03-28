package com.example.bookmoth.domain.model.profile;

public class UsernameResponse {
    private boolean exists;
    private String message;

    public boolean isExists() {
        return exists;
    }

    public String getMessage() {
        return message;
    }
}

