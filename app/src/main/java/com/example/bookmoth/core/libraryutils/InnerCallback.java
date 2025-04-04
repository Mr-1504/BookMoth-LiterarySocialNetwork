package com.example.bookmoth.core.libraryutils;

public interface InnerCallback<T> {
    void onSuccess(T body);
    void onError(String errorMessage);
}
