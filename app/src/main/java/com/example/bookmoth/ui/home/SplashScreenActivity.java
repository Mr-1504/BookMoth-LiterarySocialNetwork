package com.example.bookmoth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.bookmoth.ui.error.LoginFailedActivity;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.ProfileViewModel;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 1500;
    private static final int RETRY_DELAY = 10000;

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

    private void getProfile() {
        ProfileViewModel profileViewModel = new ProfileViewModel(new ProfileUseCase(new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                finish();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(
                        SplashScreenActivity.this,
                        getString(R.string.your_session_has_expired),
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onErrorConnectToServer(String error) {
                Toast.makeText(
                        SplashScreenActivity.this,
                        "Lỗi kết nối, thử lại sau 10 giây...",
                        Toast.LENGTH_SHORT
                ).show();

                Intent intent = new Intent(SplashScreenActivity.this, LoginFailedActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
        finish();
    }
}