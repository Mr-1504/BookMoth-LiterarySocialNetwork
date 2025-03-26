package com.example.bookmoth.ui.activity.post;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.home.HomeActivity;
import com.example.bookmoth.ui.activity.post.PinBooksActivity;
import com.example.bookmoth.ui.viewmodel.post.FlaskViewModel;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.http.Url;

public class EditPostActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_BOOK_REQUEST = 100;
    private int selectedWorkId = -1;
    private static final String SUPABASE_URL = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/";
    private EditText edtTitle, edtContent;
    private Button btnChooseFile, btnSubmitPost, btnPinBooks;
    private Uri fileUri, urlCheck = null;
    private String fileType;
    private ImageView imgView;
    private ProgressBar progressBar;
    private PostViewModel postViewModel;
    private FlaskViewModel flaskViewModel;
    private Post post = new Post();
    private ImageButton btnBack;
    private String profileId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_post);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnPinBooks = findViewById(R.id.btnChooseBook);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        btnPinBooks.setOnClickListener(view -> {
            Intent intent = new Intent(EditPostActivity.this, PinBooksActivity.class);
            startActivityForResult(intent, PICK_BOOK_REQUEST);
        });
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        flaskViewModel = new FlaskViewModel(new FlaskUseCase(new FlaskRepositoryImpl()));
        int postId = getIdPost();
        loadEdit(postId);
        btnBack.setOnClickListener(view -> finish());
        btnChooseFile.setOnClickListener(view -> openFilePicker());
        btnSubmitPost.setOnClickListener(view -> {
            btnSubmitPost.setEnabled(false);  // Vô hiệu hóa nút đăng bài
            progressBar.setVisibility(View.VISIBLE);
            if (fileUri != null && fileUri!=urlCheck) {
                urlCheck = null;
                uploadFileToSupabase(fileUri, postId);
            } else {
                urlCheck = null;
                savePostToDatabase(post.getMediaUrl(), postId);
            }
        });
    }

    private void loadEdit(int postId) {
        postViewModel.getPostById("eq." + postId, new PostViewModel.OnGetPost() {
            @Override
            public void onGetPostSuccess(List<Post> posts) {
                post = posts.get(0);
                edtTitle.setText(post.getTitle());
                edtContent.setText(post.getContent());
                selectedWorkId = post.getTab_works();
                fileUri = Uri.parse(post.getMediaUrl());
                urlCheck = fileUri;
                if (selectedWorkId != -1) {
                    fetchProductImage(selectedWorkId);
                }
                imgView = findViewById(R.id.imageView1);
                imgView.setVisibility(View.VISIBLE);
                Glide.with(EditPostActivity.this).load(post.getMediaUrl()).into(imgView);
            }

            @Override
            public void onGetPostFailure(String message) {
                Log.e("Supabase", "Lỗi kết nối API");
                Toast.makeText(EditPostActivity.this, "Không thể tải bài đăng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getIdPost(){
        Intent intentPost = getIntent();
        int postId = intentPost.getIntExtra("postID", -1);
        return postId;
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            fileType = getFileType(fileUri);
            imgView = findViewById(R.id.imageView1);
            imgView.setVisibility(View.VISIBLE);
            Glide.with(this).load(fileUri).into(imgView);
            if (fileUri != null) {
                Toast.makeText(this, "Đã chọn: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Không thể lấy tệp", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_BOOK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedWorkId = data.getIntExtra("work_id", -1);
            if (selectedWorkId != -1) {
                fetchProductImage(selectedWorkId);
            }
        }
    }
    private void fetchProductImage(int workId) {
        flaskViewModel.getBookById(workId, new FlaskViewModel.OnGetBookId() {
            @Override
            public void onGetBookSuccess(Book book) {
                if (book != null && book.getCover_url() != null) {
                    String imageUrl = book.getCover_url();
                    ImageView imageView2 = findViewById(R.id.imageView2);
                    imageView2.setVisibility(View.VISIBLE);

                    Glide.with(EditPostActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_image) // Hình mặc định nếu load lâu
                            .error(R.drawable.error_image) // Hình lỗi nếu ảnh không tồn tại
                            .into(imageView2);
                } else {
                    Log.e("API_ERROR", "Ảnh sách không tồn tại");
                }
            }

            @Override
            public void onGetBookFailure(String message) {
                Log.e("API_ERROR", "Lỗi lấy ảnh sách: " + message);
            }
        });
    }


    private void uploadFileToSupabase(Uri fileUri, int postId) {
        if (fileUri == null) {
            Toast.makeText(this, "Không thể lấy URI của file", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            byte[] fileBytes = new byte[inputStream.available()];
            inputStream.read(fileBytes);
            inputStream.close();

            String mimeType = getContentResolver().getType(fileUri);
            if (mimeType == null) mimeType = "application/octet-stream";

            RequestBody requestBody = RequestBody.create(MediaType.parse(mimeType), fileBytes);

            String bucket = "media";
            String fileExtension = getFileExtension(fileUri);  // Lấy phần mở rộng của file
            String filePathInStorage = "posts/" + System.currentTimeMillis() + ".png";  // Thêm phần mở rộng vào cuối đường dẫn
            String uploadUrl = SUPABASE_URL + "storage/v1/object/" + bucket + "/" + filePathInStorage;


            postViewModel.uploadFile(uploadUrl, requestBody, new PostViewModel.OnStorageListener() {
                @Override
                public void onStorageSuccess(String message) {
                    String fileUrl = SUPABASE_URL + "storage/v1/object/public/" + bucket + "/" + filePathInStorage;
                    savePostToDatabase(fileUrl,postId);
                }

                @Override
                public void onStorageUnSuccess(String message) {
                    btnSubmitPost.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(EditPostActivity.this, "Lỗi tải lên: " + message, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onStorageFailure(String message) {
                    Toast.makeText(EditPostActivity.this,message,Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đọc file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        String extension = "";
        String path = uri.getPath();
        if (path != null && path.contains(".")) {
            extension = path.substring(path.lastIndexOf("."));
        }
        return extension;  // Trả về phần mở rộng của file (ví dụ: .jpg, .mp4, .mp3)
    }

    private void getProfile() {
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
        );

        profileViewModel.getProfile(EditPostActivity.this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                SecureStorage.saveToken("profileId", profile.getProfileId());
                profileId = profile.getProfileId();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(EditPostActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void savePostToDatabase(String fileUrl, int postId) {
        String title = edtTitle.getText().toString().trim();
        String content = edtContent.getText().toString().trim();

        profileId = SecureStorage.getToken("profileId");
        getProfile();
        int authorId = Integer.parseInt(profileId);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());

        Map<String, Object> updatedPost = new HashMap<>();
        updatedPost.put("author_id", authorId);
        updatedPost.put("title", title);
        updatedPost.put("content", content);
        updatedPost.put("media_url", fileUrl);
        updatedPost.put("media_type", fileType);
        updatedPost.put("timestamp", timestamp);
        updatedPost.put("tab_works", selectedWorkId);

        postViewModel.updatePost("eq."+postId, updatedPost, new PostViewModel.OnSupbaBaseListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditPostActivity.this, "Cập nhật bài viết thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditPostActivity.this, HomeActivity.class);
                intent.putExtra("reload", true);
                startActivity(intent);
                finish();
            }

            public void onUnSuccess(String message) {
                btnSubmitPost.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditPostActivity.this, "Lỗi cập nhật bài viết: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                btnSubmitPost.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditPostActivity.this, "Lỗi kết nối: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String getFileType(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            if (mimeType.startsWith("image")) return "image";
            if (mimeType.startsWith("video")) return "video";
            if (mimeType.startsWith("audio")) return "audio";
        }
        return "unknown";
    }
}