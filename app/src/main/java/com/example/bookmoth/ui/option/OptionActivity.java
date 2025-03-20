package com.example.bookmoth.ui.option;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.profile.ProfileActivity;
import com.example.bookmoth.ui.wallet.PayActivity;
import com.example.bookmoth.ui.wallet.WalletActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class OptionActivity extends AppCompatActivity {

    private Button btnProfile, btnWallet, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnViewProfile);
        btnWallet = findViewById(R.id.btnWallet);

        clickLogout();
        clickWallet();
        clickProfile();
    }

    private void clickProfile() {
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void clickWallet() {
        btnWallet.setOnClickListener(v -> {
            Intent intent = new Intent(this, PayActivity.class);
            startActivity(intent);
        });
    }

    private void clickLogout() {
        btnLogout.setOnClickListener(v -> {
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                    .requestEmail()
                    .build();

            GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
            client.signOut();
            SecureStorage.clearToken();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}