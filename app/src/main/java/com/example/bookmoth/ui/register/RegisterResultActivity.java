package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.profile.SetAvatarActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

public class RegisterResultActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView tvWelcome, countdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        avatar = findViewById(R.id.imgAvatar);
        tvWelcome = findViewById(R.id.tvWelcome);
        countdown = findViewById(R.id.countdown);

        ProfileViewModel profileViewModel = new ProfileViewModel(new ProfileUseCase(new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                tvWelcome.setText(profile.getFirstName() + " " + getString(R.string.welcom_to_bookmoth));

                Glide.with(RegisterResultActivity.this)
                        .load(profile.getAvatar())
                        .placeholder(R.drawable.avatar)
                        .into(avatar);
                new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        countdown.setText(getString(R.string.continue_after) + " " + millisUntilFinished / 1000 + "s");
                    }

                    public void onFinish() {
                        setAvatar();
                    }
                }.start();
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(RegisterResultActivity.this, error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorConnectToServer(String error) {

            }
        });
    }

    private void setAvatar(){
        Intent intent = new Intent(RegisterResultActivity.this, SetAvatarActivity.class);
        startActivity(intent);
        finish();
    }
}