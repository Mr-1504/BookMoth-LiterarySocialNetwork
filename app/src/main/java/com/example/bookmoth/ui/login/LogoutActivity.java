package com.example.bookmoth.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends AppCompatActivity {

    private TextView id, idvalue, email, emailValue, name, nameValue, serverAuthCode, serverAuthCodeValue;
    private ImageView avatar;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = findViewById(R.id.Id);
        idvalue = findViewById(R.id.IdValue);
        email = findViewById(R.id.email);
        emailValue = findViewById(R.id.EmailValue);
        name = findViewById(R.id.name);
        nameValue = findViewById(R.id.nameValue);
        serverAuthCode = findViewById(R.id.serverAuthCode);
        serverAuthCodeValue = findViewById(R.id.codeValue);
        logout = findViewById(R.id.logout);
        avatar = findViewById(R.id.avatar);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();
            String id = account.getId();
            String serverAuthCode = account.getServerAuthCode();
            String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";

            nameValue.setText("Name: " + name);
            emailValue.setText("Email: " + email);
            idvalue.setText("ID: " + id);
            serverAuthCodeValue.setText("Server Auth Code: " + serverAuthCode);


            Glide.with(this)
                    .load(photoUrl)
                    .into(avatar);
        }
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                GoogleSignIn.getClient(LogoutActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }
}