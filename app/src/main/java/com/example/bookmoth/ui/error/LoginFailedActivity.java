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
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

public class LoginFailedActivity extends AppCompatActivity {

    private static final int RETRY_DELAY = 5000; // 10 giây
    private Handler retryHandler = new Handler(Looper.getMainLooper());
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_failed);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        errorMessage = findViewById(R.id.error_message);
        errorMessage.setText("Lỗi kết nối server, đang thử lại...");

        retryGetProfile();
    }

    private void retryGetProfile() {
        ProfileViewModel profileViewModel = new ProfileViewModel(new ProfileUseCase(new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                Toast.makeText(
                        LoginFailedActivity.this,
                        getString(R.string.connection_restored),
                        Toast.LENGTH_SHORT
                ).show();
                SecureStorage.saveToken("profileId", profile.getProfileId());
                Intent intent = new Intent(LoginFailedActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(
                        LoginFailedActivity.this,
                        getString(R.string.your_session_has_expired),
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(LoginFailedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onErrorConnectToServer(String error) {
                Toast.makeText(
                        LoginFailedActivity.this,
                        "Lỗi kết nối, thử lại sau " + RETRY_DELAY/1000 + " giây...",
                        Toast.LENGTH_SHORT
                ).show();

                retryHandler.postDelayed(() -> retryGetProfile(), RETRY_DELAY);
            }
        });
    }
}