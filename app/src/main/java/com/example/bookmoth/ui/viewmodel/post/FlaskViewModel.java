package com.example.bookmoth.ui.viewmodel.post;

import android.content.Context;

import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlaskViewModel {
    FlaskUseCase flaskUseCase;

    public FlaskViewModel(FlaskUseCase flaskUseCase) {
        this.flaskUseCase = flaskUseCase;
    }

    public void getBooks(Context context, final OnGetBook listener) {
        flaskUseCase.getBooks().enqueue(new Callback<ApiResponse<List<Book>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Book>>> call, Response<ApiResponse<List<Book>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getData(); // Lấy danh sách sách từ ApiResponse
                    if (books != null) {
                        listener.onGetBookSuccess(books);
                    } else {
                        listener.onGetBookFailure("Data is null");
                    }
                } else {
                    listener.onGetBookFailure("Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Book>>> call, Throwable t) {
                listener.onGetBookFailure(t.getMessage()); // Trả về lỗi cụ thể
            }
        });
    }


    public void getBookById(int id, final OnGetBookId listener) {
        flaskUseCase.getBookById(id).enqueue(new Callback<ApiResponse<Book>>() {
            @Override
            public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Book> apiResponse = response.body();

                    if (apiResponse.getData() != null) {
                        Book book = apiResponse.getData();
                        listener.onGetBookSuccess(book);
                    } else {
                        listener.onGetBookFailure("Dữ liệu sách rỗng từ API");
                    }
                } else {
                    listener.onGetBookFailure("Lỗi phản hồi từ API: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                listener.onGetBookFailure("Lỗi kết nối API: " + t.getMessage());
            }
        });
    }

    public interface OnGetBook {
        void onGetBookSuccess(List<Book> books);
        void onGetBookFailure(String message);
    }
    public interface OnGetBookId {
        void onGetBookSuccess(Book book);
        void onGetBookFailure(String message);
    }
}
