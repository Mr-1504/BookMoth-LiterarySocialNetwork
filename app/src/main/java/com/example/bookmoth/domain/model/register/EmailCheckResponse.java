package com.example.bookmoth.domain.model.register;

public class EmailCheckResponse {
    private boolean exists;
    private String message;

    public boolean isExists() {
        return exists;
    }

    public String getMessage() {
        return message;
    }
}