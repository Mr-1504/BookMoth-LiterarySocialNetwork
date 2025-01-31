package com.example.domain.model;

public class User {
    private int id;
    private String Email;
    private String HashedPassword;

    public User(int id, String email, String hashedPassword) {
        this.id = id;
        this.Email = email;
        this.HashedPassword = hashedPassword;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return Email;
    }

    public String getHashedPassword() {
        return HashedPassword;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setHashedPassword(String hashedPassword) {
        HashedPassword = hashedPassword;
    }
}
