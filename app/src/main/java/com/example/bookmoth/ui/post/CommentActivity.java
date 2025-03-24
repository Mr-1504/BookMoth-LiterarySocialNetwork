package com.example.bookmoth.ui.post;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.adapter.CommentAdapter;
import com.example.bookmoth.ui.adapter.PostAdapter;
import com.example.bookmoth.domain.model.post.Comment;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageButton btnBack, btnSend;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private EditText editTextComment;

//    private final PostUseCase postUsecase = new PostUseCase(new SupabaseRepositoryImpl());
    private PostViewModel postViewModel;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comment);

        View rootView = findViewById(R.id.main);
        final LinearLayout commentBar = findViewById(R.id.commentBar);
        recyclerView = findViewById(R.id.recyclerViewComments);
        btnBack = findViewById(R.id.imageButtonBack);
        btnSend = findViewById(R.id.imageButtonSend);
        editTextComment = findViewById(R.id.editTextComment);
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(this, postList, new PostUseCase(new SupabaseRepositoryImpl()),new FlaskUseCase(new FlaskRepositoryImpl()));
        commentAdapter = new CommentAdapter(this,commentList,new FlaskUseCase(new FlaskRepositoryImpl()));
        ConcatAdapter concatAdapter = new ConcatAdapter(postAdapter, commentAdapter);
        recyclerView.setAdapter(concatAdapter);

        Intent intentGetPostId = getIntent();
        int postID = intentGetPostId.getIntExtra("postID",0);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thay đổi bàn phím để điều chỉnh commentBar
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    commentBar.animate().translationY(-keypadHeight).setDuration(200).start();
                } else {
                    commentBar.animate().translationY(0).setDuration(200).start();
                }
            }
        });
        btnBack.setOnClickListener(view -> finish());

        btnSend.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                sendComment(commentText,postID);
            } else {
                Toast.makeText(CommentActivity.this, "Vui lòng nhập bình luận!", Toast.LENGTH_SHORT).show();
            }
        });
        loadPost(postID);
        recyclerView.addItemDecoration(new PostDividerItemDecoration(this, 30, android.R.color.darker_gray));
        loadComments(postID);

    }

    private void sendComment(String commentText, int postId) {
        String id = SecureStorage.getToken("profileId");
        if (id == null) {
            Log.e("Supabase", "Không tìm thấy profileId trong SecureStorage");
            getProfile();
            id = SecureStorage.getToken("profileId");
            return;
        }
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("user_id", Integer.parseInt(id));
        newComment.put("post_id", postId);
        newComment.put("content", commentText);

        postViewModel.addComment(newComment, new PostViewModel.OnSupbaBaseListener() {
            @Override
            public void onSuccess() {
                Log.d("Supabase", "Thêm bình luận thành công!");
                Toast.makeText(CommentActivity.this, "Bình luận đã được gửi!", Toast.LENGTH_SHORT).show();
                editTextComment.setText("");
                loadComments(postId);
                updateCountComment(postId);
            }

            @Override
            public void onUnSuccess(String message) {
                Toast.makeText(CommentActivity.this, "Lỗi khi gửi bình luận!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Log.e("Supabase", "Lỗi kết nối API", new Throwable(message));
                Toast.makeText(CommentActivity.this, "Không thể gửi bình luận. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Log.d("Supabase", "Thêm bình luận thành công!");
//                    Toast.makeText(CommentActivity.this, "Bình luận đã được gửi!", Toast.LENGTH_SHORT).show();
//                    editTextComment.setText("");
//                    loadComments(postId);
//                    updateCountComment(postId);
//                } else {
//                    try {
//                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Lỗi không xác định";
//                        Log.e("Supabase", "Lỗi khi thêm bình luận: " + errorBody);
//                        Toast.makeText(CommentActivity.this, "Lỗi khi gửi bình luận!", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Log.e("Supabase", "Lỗi kết nối API", t);
//                Toast.makeText(CommentActivity.this, "Không thể gửi bình luận. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private void loadPost(int postID) {
        postViewModel.getPostById("eq." + postID, new PostViewModel.OnGetPost() {
            @Override
            public void onGetPostSuccess(List<Post> posts) {
                postList.clear();
                postList.add(posts.get(0)); // Lấy bài đăng đầu tiên
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetPostFailure(String message) {
                Log.e("Supabase", "Lỗi kết nối API");
                Toast.makeText(CommentActivity.this, "Không thể tải bài đăng!", Toast.LENGTH_SHORT).show();
            }
        });
//        .enqueue(new Callback<List<Post>>() {
//            @Override
//            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
//                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
//                    postList.clear();
//                    postList.add(response.body().get(0)); // Lấy bài đăng đầu tiên
//                    postAdapter.notifyDataSetChanged();
//                } else {
//                    Log.e("Supabase", "Lỗi lấy bài đăng: " + response.errorBody()+"eq." + postID);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Post>> call, Throwable t) {
//                Log.e("Supabase", "Lỗi kết nối API", t);
//                Toast.makeText(CommentActivity.this, "Không thể tải bài đăng!", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    private void loadComments(int postID){
        postViewModel.getCommentForId("eq."+postID, new PostViewModel.OnGetComment() {
            @Override
            public void onGetCommentSuccess(List<Comment> comments) {
                Log.d("Supabase", "Số lượng bình luận: " + comments.size());
                commentList.clear();
                commentList.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetCommentUnSuccess(String message) {

            }

            @Override
            public void onGetCommentFailure(String message) {

            }
        });

//                .enqueue(new Callback<List<Comment>>() {
//            @Override
//            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
//                if(response.isSuccessful()&& response.body()!=null){
//                    List<Comment> comments = response.body();
//                    Log.d("Supabase", "Số lượng bình luận: " + comments.size());
//                    commentList.clear();
//                    commentList.addAll(comments);
//                    commentAdapter.notifyDataSetChanged();
//                }
//                else {
//                    try {
//                        Log.e("Supabase", "Lỗi lấy bình luận: " + response.errorBody().string());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Comment>> call, Throwable t) {
//                Log.e("Supabase", "Lỗi kết nối API", t);
//                Toast.makeText(CommentActivity.this, "Không thể tải bình luận. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
//            }
//
//        });
    }
    public void updateCountComment(int idPost) {
        postViewModel.getCommentForId("eq." + idPost, new PostViewModel.OnGetComment() {
            @Override
            public void onGetCommentSuccess(List<Comment> comments) {
                int newCount = comments.size();
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("count_comment", newCount);

                postViewModel.updateComment("eq." + idPost, updateData, new PostViewModel.OnSupbaBaseListener() {
                    @Override
                    public void onSuccess() {
                        loadPost(idPost);
                    }

                    @Override
                    public void onUnSuccess(String message) {
                        Log.e("Supabase", "Lỗi cập nhật comment: " + message);
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e("Supabase", "Lỗi kết nối khi cập nhật like", new Throwable(message));
                    }
                });
//                            .enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if (response.isSuccessful()) {
//                                loadPost(idPost);
//                            } else {
//                                Log.e("API", "Lỗi cập nhật comment: " + response.errorBody());
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//                            Log.e("API", "Lỗi kết nối khi cập nhật like", t);
//                        }
//                    });
            }

            @Override
            public void onGetCommentUnSuccess(String message) {
                Toast.makeText(CommentActivity.this, "Lỗi khi lấy số lượng comment", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGetCommentFailure(String message) {
                Toast.makeText(CommentActivity.this, "Lỗi kết nối khi lấy số lượng comment", Toast.LENGTH_SHORT).show();
            }
        });
//                .enqueue(new Callback<List<Comment>>() {
//            @Override
//            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Comment>> call, Throwable t) {
//                Log.e("API", "Lỗi kết nối khi lấy số lượng comment", t);
//            }
//        });
    }
    private void getProfile() {
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
        );

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                SecureStorage.saveToken("profileId", profile.getProfileId());
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(CommentActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}