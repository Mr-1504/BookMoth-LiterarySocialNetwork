package com.example.postapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.postapp.api.ApiClient;
import com.example.postapp.api.SupabaseApi;

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
    private EditText edtTitle, edtContent;
    private Button btnChooseFile, btnSubmitPost;
    private Uri fileUri;
    private String fileType;
    private ImageView imgView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtContent);
        btnChooseFile = findViewById(R.id.btnChooseFile);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        progressBar = findViewById(R.id.progressBar);
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
            String uploadUrl = SupabaseApi.STORAGE_URL + "object/" + bucket + "/" + filePathInStorage;

            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + SupabaseApi.SUPABASE_KEY);
            headers.put("apikey", SupabaseApi.SUPABASE_KEY);
            headers.put("Content-Type", mimeType);

            ApiClient.getStorageApi().uploadFile(uploadUrl, headers, requestBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        String fileUrl = SupabaseApi.STORAGE_URL + "object/public/" + bucket + "/" + filePathInStorage;
                        savePostToDatabase(fileUrl);
                    } else {
                        btnSubmitPost.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có phản hồi";
                            Toast.makeText(CreatePostActivity.this, "Lỗi tải lên: " + errorBody, Toast.LENGTH_SHORT).show();
                            Log.e("UPLOAD_ERROR", "Response: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(CreatePostActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                    Log.e("UPLOAD_ERROR", "Lỗi: ", t);
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

        ApiClient.getDatabaseApi().createPost(newPost).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreatePostActivity.this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreatePostActivity.this, MainActivity.class);
                    intent.putExtra("reload", true);
                    startActivity(intent);
                    finish();

                } else {
                    btnSubmitPost.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Không có phản hồi";
                        Toast.makeText(CreatePostActivity.this, "Lỗi lưu bài viết: " + errorBody, Toast.LENGTH_SHORT).show();
                        Log.e("SAVE_POST_ERROR", "Response: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                Log.e("SAVE_POST_ERROR", "Lỗi: ", t);
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
