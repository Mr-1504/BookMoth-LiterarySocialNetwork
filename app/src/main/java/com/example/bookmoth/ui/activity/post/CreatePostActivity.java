package com.example.bookmoth.ui.activity.post;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.remote.post.Api;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.home.HomeActivity;
import com.example.bookmoth.ui.viewmodel.post.FlaskViewModel;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreatePostActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;
    private static final int PICK_BOOK_REQUEST = 100;
    private int selectedWorkId = -1;
    private static final String SUPABASE_URL = "https://vhqcdiaoqrlcsnqvjpqh.supabase.co/";
    private EditText edtTitle, edtContent;
    private Button btnChooseFile, btnSubmitPost, btnPinBooks;
    private Uri fileUri;
    private String fileType;
    private ImageView imgView;
    private ProgressBar progressBar;
    private PostViewModel postViewModel;
    private FlaskViewModel flaskViewModel;
    private ImageButton btnBack;
    private String profileId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        btnPinBooks = findViewById(R.id.btnChooseBook);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        btnPinBooks.setOnClickListener(view -> {
            Intent intent = new Intent(CreatePostActivity.this, PinBooksActivity.class);
            startActivityForResult(intent, PICK_BOOK_REQUEST);
        });

        btnBack.setOnClickListener(view -> finish());
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        flaskViewModel = new FlaskViewModel(new FlaskUseCase(new FlaskRepositoryImpl()));
        btnChooseFile.setOnClickListener(view -> openFilePicker());
        btnSubmitPost.setOnClickListener(view -> {
            btnSubmitPost.setEnabled(false);  // Vô hiệu hóa nút đăng bài
            progressBar.setVisibility(View.VISIBLE);
            if (fileUri != null) {
                uploadFileToSupabase(fileUri);
            } else {
                savePostToDatabase(null);
            }
        });
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
            Map<String, Object> body = new HashMap<>();
            body.put("image", fileUri);
            profileId = SecureStorage.getToken("profileId");
            getProfile();

            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                if (inputStream != null) {
                    byte[] fileBytes = getBytesFromInputStream(inputStream);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileBytes);
                    MultipartBody.Part body1 = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);

                    flaskViewModel.getCheckBlood(Integer.parseInt(profileId), body1, new FlaskViewModel.OnGetCheckBlood() {
                        @Override
                        public void onGetSuccess(int checkBlood) {
                            if (checkBlood == 1) {
                                btnSubmitPost.setEnabled(false);
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(CreatePostActivity.this, "Đang kiểm tra nội dung...", Toast.LENGTH_SHORT).show();

                                flaskViewModel.getProcessImage(Integer.parseInt(profileId), body1, new FlaskViewModel.OnGetProcessImage() {
                                    @Override
                                    public void onGetSuccess(byte[] imageBytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                        imgView.setImageBitmap(bitmap);
                                        Toast.makeText(CreatePostActivity.this, "Kiểm tra nội dung thành công", Toast.LENGTH_SHORT).show();
                                        btnSubmitPost.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onGetFailure(String message) {
                                        btnSubmitPost.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(CreatePostActivity.this, "Lỗi kiểm tra nội dung: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                btnSubmitPost.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(CreatePostActivity.this, "Không phát hiện máu", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onGetFailure(String message) {
                            btnSubmitPost.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreatePostActivity.this, "Lỗi kiểm tra điều kiện: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "Không thể đọc file", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(this, "Lỗi khi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            Glide.with(this).load(fileUri).into(imgView);
            Toast.makeText(this, "Đã chọn: " + fileUri.toString(), Toast.LENGTH_SHORT).show();
        }
        if (requestCode == PICK_BOOK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedWorkId = data.getIntExtra("work_id", -1);
            if (selectedWorkId != -1) {
                fetchProductImage(selectedWorkId);
            }
        }
    }
    // Hàm chuyển InputStream thành byte[]
    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        inputStream.close();
        return byteBuffer.toByteArray();
    }
    private void fetchProductImage(int workId) {
        flaskViewModel.getBookById(workId, new FlaskViewModel.OnGetBookId() {
            @Override
            public void onGetBookSuccess(Book book) {
                if (book != null && book.getCover_url() != null) {
                    String imageUrl = book.getCover_url();
                    ImageView imageView2 = findViewById(R.id.imageView2);
                    imageView2.setVisibility(View.VISIBLE);

                    Glide.with(CreatePostActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
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

    private void uploadFileToSupabase(Uri fileUri) {
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
                            savePostToDatabase(fileUrl);
                        }

                        @Override
                        public void onStorageUnSuccess(String message) {
                            btnSubmitPost.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(CreatePostActivity.this, "Lỗi tải lên: " + message, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onStorageFailure(String message) {
                            Toast.makeText(CreatePostActivity.this,message,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
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

        profileViewModel.getProfile(CreatePostActivity.this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                SecureStorage.saveToken("profileId", profile.getProfileId());
                profileId = profile.getProfileId();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(CreatePostActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void savePostToDatabase(String fileUrl) {
        String title = edtTitle.getText().toString().trim();
        String content = edtContent.getText().toString().trim();

        profileId = SecureStorage.getToken("profileId");
        getProfile();
        int authorId = Integer.parseInt(profileId);  // Bạn có thể lấy từ user hiện tại nếu có authentication
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(new Date());

        Map<String, Object> newPost = new HashMap<>();
        newPost.put("author_id", authorId);
        newPost.put("title", title);
        newPost.put("content", content);
        newPost.put("media_url", fileUrl);
        newPost.put("media_type", fileType);
        newPost.put("timestamp", timestamp);
        newPost.put("tab_works", selectedWorkId);

        postViewModel.createPost(newPost, new PostViewModel.OnSupbaBaseListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(CreatePostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreatePostActivity.this, HomeActivity.class);
                intent.putExtra("reload", true);
                startActivity(intent);
                finish();
            }

            public void onUnSuccess(String message) {
                btnSubmitPost.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CreatePostActivity.this, "Lỗi lưu bài viết: ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                btnSubmitPost.setEnabled(true);
                progressBar.setVisibility(View.GONE);
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
