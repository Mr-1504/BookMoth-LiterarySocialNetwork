package com.example.bookmoth.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.error.LoginFailedActivity;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

/**
 * Màn hình splash screen của ứng dụng, kiểm tra token và điều hướng đến màn hình phù hợp.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(() -> {
            String token = SecureStorage.getToken("refresh_token");

            if (token != null && !token.isEmpty()) {
                getProfile();
            } else {
                navigateToLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    /**
     * Gọi API để lấy thông tin hồ sơ người dùng nếu token hợp lệ.
     * Nếu thành công, chuyển đến màn hình thanh toán.
     * Nếu thất bại, chuyển đến màn hình đăng nhập hoặc hiển thị thông báo lỗi.
     */
    private void getProfile() {
        ProfileViewModel profileViewModel = new ProfileViewModel(new ProfileUseCase(new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                SecureStorage.saveToken("profileId", profile.getProfileId());
                startActivity(intent);
                getPermission();
                finish();
            }

            @Override
            public void onProfileFailure(String error) {
                navigateToLogin();
            }

            @Override
            public void onErrorConnectToServer(String error) {
                Intent intent = new Intent(SplashScreenActivity.this, LoginFailedActivity.class);
                startActivity(intent);
                getPermission();
                finish();
            }
        });
    }

    /**
     * Chuyển hướng người dùng đến màn hình đăng nhập nếu không có token hoặc token hết hạn.
     */
    private void navigateToLogin() {
        Toast.makeText(
                SplashScreenActivity.this,
                getString(R.string.your_session_has_expired),
                Toast.LENGTH_SHORT
        ).show();
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        getPermission();
        finish();
    }

    /**
     * Yêu cầu quyền gửi thông báo cho thiết bị nếu chạy Android 13 trở lên.
     */
    private void getPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    /**
     * Xử lý kết quả khi người dùng cấp quyền hoặc từ chối quyền thông báo.
     *
     * @param requestCode   Mã yêu cầu quyền.
     * @param permissions   Danh sách quyền được yêu cầu.
     * @param grantResults  Kết quả cấp quyền.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FCM", "Quyền thông báo đã được cấp!");
            } else {
                Log.e("FCM", "Người dùng từ chối quyền thông báo!");
            }
        }
    }

}