package com.example.bookmoth.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.jsibbold.zoomage.ZoomageView;

import java.io.IOException;

public class ChooseAvatarActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ZoomageView imgAvatar;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);

        imgAvatar = findViewById(R.id.imgAvatar);
        btnSave = findViewById(R.id.btnSave);

        // Mở thư viện ảnh khi nhấn vào ảnh đại diện
        imgAvatar.setOnClickListener(view -> openGallery());

        // Xử lý khi nhấn nút lưu
        btnSave.setOnClickListener(view -> saveAvatar());
    }

    // Hàm mở thư viện ảnh
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Nhận kết quả từ thư viện ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                // Đọc ảnh từ URI và hiển thị
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgAvatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Lỗi khi chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Giả lập lưu ảnh (ở đây chỉ là thông báo)
    private void saveAvatar() {
        Toast.makeText(this, "Ảnh đã được lưu!", Toast.LENGTH_SHORT).show();
    }
}