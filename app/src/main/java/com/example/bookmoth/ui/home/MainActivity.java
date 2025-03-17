package com.example.bookmoth.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.payment.PayActivity;
import com.example.bookmoth.ui.register.OptionActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.common.api.Scope;

public class MainActivity extends AppCompatActivity {


    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button but = findViewById(R.id.payment);
        but.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PayActivity.class);
            startActivity(intent);
            finish();
        });

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(MainActivity.this, signInOptions);

        //Profile _profile = (Profile) getIntent().getSerializableExtra("profile");

//        TextView textView = findViewById(R.id.textView);
//        textView.setText(_profile.getLastName() + " " + _profile.getFirstName());

        Button logout = findViewById(R.id.logout);
        Button profile = findViewById(R.id.btnProfile);

        logout.setOnClickListener(view -> {
            SecureStorage.clearToken();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            client.signOut();
            finish();
        });
    }
}