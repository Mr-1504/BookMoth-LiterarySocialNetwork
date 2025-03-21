package com.example.bookmoth.ui.post;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.data.remote.post.ApiResponse;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.domain.model.post.Book;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.ui.home.HomeActivity;
import com.example.bookmoth.ui.viewmodel.post.FlaskViewModel;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Toast.makeText(this, "Vui lòng chọn tệp", Toast.LENGTH_SHORT).show();
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

                    Glide.with(CreatePostActivity.this)
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

//    private void fetchProductImage(int workId) {

//        ApiClient.getClient().getBookById(workId).enqueue(new Callback<ApiResponse<Book>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<Book> apiResponse = response.body(); // Nhận response từ API
//                    Book book = apiResponse.getData(); // Lấy dữ liệu từ response
//
//                    if (book != null) {
//                        String imageUrl = book.getCover_url();
//                        ImageView imageView2 = findViewById(R.id.imageView2);
//                        imageView2.setVisibility(View.VISIBLE);
//
//                        Glide.with(CreatePostActivity.this)
//                                .load(imageUrl)
//                                .into(imageView2);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
//                Log.e("API_ERROR", "Lỗi lấy ảnh sản phẩm: " + t.getMessage());
//            }
//        });
//    }
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
                        }
                    });

//                    .enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//                        String fileUrl = SUPABASE_URL + "object/public/" + bucket + "/" + filePathInStorage;
//                        savePostToDatabase(fileUrl);
//                    } else {
//                        btnSubmitPost.setEnabled(true);
//                        progressBar.setVisibility(View.GONE);
//                        try {
//                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có phản hồi";
//                            Toast.makeText(CreatePostActivity.this, "Lỗi tải lên: " + errorBody, Toast.LENGTH_SHORT).show();
//                            Log.e("UPLOAD_ERROR", "Response: " + errorBody);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    Toast.makeText(CreatePostActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//                    Log.e("UPLOAD_ERROR", "Lỗi: ", t);
//                }
//            });

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

    private void savePostToDatabase(String fileUrl) {
        String title = edtTitle.getText().toString().trim();
        String content = edtContent.getText().toString().trim();
        int authorId = 1;  // Bạn có thể lấy từ user hiện tại nếu có authentication
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
//                .enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(CreatePostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(CreatePostActivity.this, HomeActivity.class);
//                    intent.putExtra("reload", true);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    btnSubmitPost.setEnabled(true);
//                    progressBar.setVisibility(View.GONE);
//                    try {
//                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có phản hồi";
//                        Toast.makeText(CreatePostActivity.this, "Lỗi lưu bài viết: " + errorBody, Toast.LENGTH_SHORT).show();
//                        Log.e("SAVE_POST_ERROR", "Response: " + errorBody);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(CreatePostActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
//                Log.e("SAVE_POST_ERROR", "Lỗi: ", t);
//            }
//        });
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
