package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.google.android.material.textfield.TextInputEditText;

public class TypeEmailActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Button nextButton, returnButton, iHaveAAccountButton;
    private TextView warningEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_email);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.edtEmail);
        warningEmail = findViewById(R.id.tvWarning);
        nextButton = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);

        clickNext();
    }

    private void clickNext() {
        nextButton.setOnClickListener(view -> {
            String emailText = email.getText().toString().trim();
            if (emailText.isEmpty()) {
                warningEmail.setVisibility(View.VISIBLE);
                warningEmail.setText(R.string.email_not_empty);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
                warningEmail.setVisibility(View.VISIBLE);
                warningEmail.setText(R.string.invalid_username);
            }
            else {
                warningEmail.setVisibility(View.GONE);
                Intent intent = new Intent(TypeEmailActivity.this, TypeOtpActivity.class);
                startActivity(intent);
            }
        });
    }
}