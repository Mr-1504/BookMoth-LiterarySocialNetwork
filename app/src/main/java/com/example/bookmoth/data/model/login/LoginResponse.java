package com.example.bookmoth.data.model.login;

public class LoginResponse {
    private boolean success;
    private String message;
    private Object data;

    public LoginResponse(boolean success, String message, Object data){
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }


    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
