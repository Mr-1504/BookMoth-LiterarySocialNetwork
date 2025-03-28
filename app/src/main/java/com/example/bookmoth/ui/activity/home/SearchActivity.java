package com.example.bookmoth.ui.home;

import static java.security.AccessController.getContext;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.ui.adapter.PostAdapter;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    public static final int POST_SEARCH = 1;
    public static final int PROFILE_SEARCH = 2;
    public static final int BOOK_SEARCH = 3;
    public static final int WORKS_SEARCH = 4;
    private ImageButton btnBack, btnSearch, btnPost, btnProfile, btnBook, btnWorks;
    private EditText edtSearch;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private PostViewModel postViewModel;
    private List<Post> postList = new ArrayList<>();
    private int searchType = POST_SEARCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        declare();
        btnBack.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnSearch.setOnClickListener(v -> {
            String search = edtSearch.getText().toString();
            if (search.isEmpty()) {
                edtSearch.setError("Please enter something to search");
                return;
            }
            // search
            if(searchType == POST_SEARCH) {

                // search post
                postAdapter = new PostAdapter(this, postList, new PostUseCase(new SupabaseRepositoryImpl()), new FlaskUseCase(new FlaskRepositoryImpl()));
                recyclerView.setAdapter(postAdapter);
                postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
                postViewModel.searchPosts(search, new PostViewModel.OnGetPost() {
                    @Override
                    public void onGetPostSuccess(List<Post> posts) {
                        postList.clear();
                        postList.addAll(posts);
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onGetPostFailure(String message) {
                        edtSearch.setError(message);
                    }
                });
            }
            else if(searchType == PROFILE_SEARCH) {
                // search profile
            }
            else if(searchType == BOOK_SEARCH) {
                // search book
            }
            else if(searchType == WORKS_SEARCH) {
                // search works
            }
        });
        btnPost.setOnClickListener(v -> {
            searchType = POST_SEARCH;
            edtSearch.setHint("Tìm bài đăng");
            btnPost.setImageResource(R.drawable.search2);
            btnProfile.setImageResource(R.drawable.search1);
            btnBook.setImageResource(R.drawable.search1);
            btnWorks.setImageResource(R.drawable.search1);
        });
        btnProfile.setOnClickListener(v -> {
            searchType = PROFILE_SEARCH;
            edtSearch.setHint("Tìm người dùng");
            btnProfile.setImageResource(R.drawable.search2);
            btnPost.setImageResource(R.drawable.search1);
            btnBook.setImageResource(R.drawable.search1);
            btnWorks.setImageResource(R.drawable.search1);
        });
        btnBook.setOnClickListener(v -> {
            searchType = BOOK_SEARCH;
            edtSearch.setHint("Tìm sách");
            btnBook.setImageResource(R.drawable.search2);
            btnProfile.setImageResource(R.drawable.search1);
            btnPost.setImageResource(R.drawable.search1);
            btnWorks.setImageResource(R.drawable.search1);
        });
        btnWorks.setOnClickListener(v -> {
            searchType = WORKS_SEARCH;
            edtSearch.setHint("Tìm tác phẩm");
            btnWorks.setImageResource(R.drawable.search2);
            btnProfile.setImageResource(R.drawable.search1);
            btnBook.setImageResource(R.drawable.search1);
            btnPost.setImageResource(R.drawable.search1);
        });
    }
    private void declare() {
        btnBack = findViewById(R.id.btnBackSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnPost = findViewById(R.id.btnPostSearch);
        btnProfile = findViewById(R.id.btnProfileSearch);
        btnBook = findViewById(R.id.btnBookSearch);
        btnWorks = findViewById(R.id.btnWorkSearch);
        edtSearch = findViewById(R.id.editTextSearch);
        recyclerView = findViewById(R.id.recyclerViewSearch);
    }
}