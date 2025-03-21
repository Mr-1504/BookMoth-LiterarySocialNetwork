package com.example.bookmoth.ui.post;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bookmoth.R;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.ui.adapter.BookAdapter;
import com.example.bookmoth.ui.viewmodel.post.FlaskViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PinBooksActivity extends AppCompatActivity {
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    ImageButton btnBack, btnSearch;
    EditText edtSearch;
    FlaskViewModel flaskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pin_books);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        flaskViewModel = new FlaskViewModel(new FlaskUseCase(new FlaskRepositoryImpl()));
        btnSearch =  findViewById(R.id.btnSearch);
        edtSearch = findViewById(R.id.textSearch);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewBooks);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        fetchBooks();
    }

    private void fetchBooks() {
        flaskViewModel.getBooks(this, new FlaskViewModel.OnGetBook() {
            @Override
            public void onGetBookSuccess(List<Book> books) {
                bookAdapter = new BookAdapter(PinBooksActivity.this, books, workId -> {
                    Log.d("BOOK_SELECTED", "Gửi work_id: " + workId);
                    Intent intent = new Intent();
                    intent.putExtra("work_id", workId);
                    setResult(RESULT_OK, intent);
                    finish();
                });
                recyclerView.setAdapter(bookAdapter);
            }

            @Override
            public void onGetBookFailure(String message) {
                Log.e("API_ERROR", "Lỗi kết nối: " + message);
            }
        });
//        ApiClient.getClient().getBooks().enqueue(new Callback<ApiResponse<List<Book>>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<List<Book>>> call, Response<ApiResponse<List<Book>>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<Book> books = response.body().getData();
//                    if (books != null && !books.isEmpty()) {
//                        bookAdapter = new BookAdapter(PinBooksActivity.this, books, workId -> {
//                            Log.d("BOOK_SELECTED", "Gửi work_id: " + workId);
//                            Intent intent = new Intent();
//                            intent.putExtra("work_id", workId);
//                            setResult(RESULT_OK, intent);
//                            finish();
//                        });
//                        recyclerView.setAdapter(bookAdapter);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<List<Book>>> call, Throwable t) {
//                Log.e("API_ERROR", "Lỗi kết nối: " + t.getMessage());
//            }
//        });
    }


}