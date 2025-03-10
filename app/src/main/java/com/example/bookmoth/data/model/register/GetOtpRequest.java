package com.example.bookmoth.data.model.register;

public class GetOtpRequest {
    private String email;
    private String name;
    private int type;

    public GetOtpRequest(String email, String name) {
        this.email = email;
        this.name = name;
//        this.type = 1;
    }
}
