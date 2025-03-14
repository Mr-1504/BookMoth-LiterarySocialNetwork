package com.example.bookmoth.data.model.register;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int gender;
    private int accountType;

    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
    }
}
