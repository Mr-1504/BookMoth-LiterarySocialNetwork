package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.bookmoth.ui.login.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class SetPasswordActivity extends AppCompatActivity {

    private Button returnButton, nextButton, iHaveAccountButton;
    private TextInputEditText passwordEditText, confirmPasswordEditText;
    private TextView warningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        returnButton = findViewById(R.id.return_button);
        nextButton = findViewById(R.id.next_for_register);
        iHaveAccountButton = findViewById(R.id.i_have_a_account);

        passwordEditText = findViewById(R.id.edtPassword);
        confirmPasswordEditText = findViewById(R.id.edtConfirmPassword);

        warningText = findViewById(R.id.tvWarning);

        submitPassword();
        clickIHaveAAccount();
        clickReturn();
    }

    private void clickReturn() {
        returnButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void clickIHaveAAccount() {
        iHaveAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void submitPassword() {
        nextButton.setOnClickListener(v -> {
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (password.isEmpty()) {
                warningText.setText(R.string.string_password_required);
                warningText.setVisibility(View.VISIBLE);
                passwordEditText.requestFocus();
                return;
            }
            if (!isValidPassword(password)) {
                warningText.setText(R.string.invalid_patten_pass);
                warningText.setVisibility(View.VISIBLE);
                passwordEditText.requestFocus();
                return;
            }
            if (confirmPassword.isEmpty()) {
                warningText.setText(R.string.string_password_required);
                warningText.setVisibility(View.VISIBLE);
                confirmPasswordEditText.requestFocus();
                return;
            }

            if (!password.equals(confirmPassword)) {
                warningText.setText(R.string.string_password_not_match);
                warningText.setVisibility(View.VISIBLE);
                confirmPasswordEditText.requestFocus();
                return;
            }


            warningText.setVisibility(View.GONE);


        });
    }

    private static boolean isValidPassword(String password) {
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$";
        return password.matches(pattern);
    }

}