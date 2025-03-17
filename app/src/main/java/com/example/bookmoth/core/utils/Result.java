package com.example.bookmoth.core.utils;

public abstract class Result<T> {
    public static final class Success<T> extends Result<T> {
        private T data;
        public Success(T data) { this.data = data; }
        public T getData() { return data; }
        public void setData(T data) { this.data = data; }
    }

    public static class Error<T> extends Result<T> {
        private int statusCode;
        private String message;

        public Error(int statusCode) {
            this.statusCode = statusCode;
            this.message = "";
        }

        public Error(String message) {
            this.statusCode = 0;
            this.message = message;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }
    }

    public static final class Loading<T> extends Result<T> {}
}
