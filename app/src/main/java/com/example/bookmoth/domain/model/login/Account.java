package com.example.bookmoth.domain.model.login;

public class Account {
    private int accountId;
    private String email;
    private String password;
    private int accountType;

    public Account(int accountId, String email, String password, int accountType) {
        this.accountId = accountId;
        this.email = email;
        this.password = password;
        this.accountType = accountType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}
