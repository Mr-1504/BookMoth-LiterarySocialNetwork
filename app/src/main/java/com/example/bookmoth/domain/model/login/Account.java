package com.example.bookmoth.domain.model.login;

public class Account {
    private int accountId;
    private String email;

    public Account(int accountId, String email) {
        this.accountId = accountId;
        this.email = email;
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
}
