package com.example.bookmoth.ui.error;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.home.HomeActivity;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.payment.PayActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

/**
 * Màn hình thông báo lỗi khi đăng nhập thất bại do lỗi kết nối server.
 * Ứng dụng sẽ tự động retry sau một khoảng thời gian để thử lấy lại profile.
 */
public class LoginFailedActivity extends AppCompatActivity {

    private static final int RETRY_DELAY = 5000; // 5 giây
    private Handler retryHandler;
    private TextView errorMessage;
    private boolean isActivityActive = true; // Biến kiểm tra trạng thái Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_failed);

        // Áp dụng insets để hỗ trợ hiển thị toàn màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        errorMessage = findViewById(R.id.error_message);
        errorMessage.setText("Lỗi kết nối server, đang thử lại...");

        retryHandler = new Handler(Looper.getMainLooper());
        retryGetProfile();
    }

    /**
     * Thử lại lấy thông tin profile từ server.
     * Nếu thành công: Chuyển sang `PayActivity`.
     * Nếu session hết hạn: Chuyển về `LoginActivity`.
     * Nếu vẫn lỗi kết nối: Hiển thị thông báo và thử lại sau `RETRY_DELAY`.
     */
    private void retryGetProfile() {
        if (!isActivityActive) return; // Kiểm tra nếu Activity đã bị hủy thì không retry

        ProfileViewModel profileViewModel = new ProfileViewModel(new ProfileUseCase(new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                if (!isActivityActive) return;

                Toast.makeText(
                        LoginFailedActivity.this,
                        getString(R.string.connection_restored),
                        Toast.LENGTH_SHORT
                ).show();

                SecureStorage.saveToken("profileId", profile.getProfileId());

                startActivity(new Intent(LoginFailedActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onProfileFailure(String error) {
                if (!isActivityActive) return;

                Toast.makeText(
                        LoginFailedActivity.this,
                        getString(R.string.your_session_has_expired),
                        Toast.LENGTH_SHORT
                ).show();

                startActivity(new Intent(LoginFailedActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onErrorConnectToServer(String error) {
                if (!isActivityActive) return;

                Toast.makeText(
                        LoginFailedActivity.this,
                        "Lỗi kết nối, thử lại sau " + (RETRY_DELAY / 1000) + " giây...",
                        Toast.LENGTH_SHORT
                ).show();

                retryHandler.postDelayed(LoginFailedActivity.this::retryGetProfile, RETRY_DELAY);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityActive = false; // Ngăn chặn retry khi Activity bị hủy
        retryHandler.removeCallbacksAndMessages(null); // Hủy mọi callback đang chờ xử lý
    }
}
