package com.example.bookmoth.data.model.register;

public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int gender;
    private String dateOfBirth;

    public RegisterRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            int gender,
            String dateOfBirth
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
