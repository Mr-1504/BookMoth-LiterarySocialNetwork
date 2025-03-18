package com.example.bookmoth.ui.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.register.RegisterRepositoryImpl;
import com.example.bookmoth.domain.usecase.register.RegisterUseCase;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class TypeEmailActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Button nextButton, returnButton, iHaveAAccountButton;
    private TextView warningEmail;
    private RegisterViewModel registerViewModel;


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

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new ViewModelProvider(this).get(RegisterViewModel.class) :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");


        clickNext();
        clickReturn();
        clickIHaveAAccount();
    }

    private void clickReturn() {
        returnButton.setOnClickListener(v -> finish());
    }

    private void clickIHaveAAccount() {
        iHaveAAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void clickNext() {
        nextButton.setOnClickListener(view -> {
            String emailText = Objects.requireNonNull(email.getText()).toString().trim();
            if (emailText.isEmpty()) {
                warningEmail.setVisibility(View.VISIBLE);
                warningEmail.setText(R.string.email_not_empty);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                warningEmail.setVisibility(View.VISIBLE);
                warningEmail.setText(R.string.invalid_username);
            } else {
                warningEmail.setVisibility(View.GONE);
                checkEmail(emailText);
            }
        });
    }

    private void checkEmail(String email) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        registerViewModel.setEmail(email);
        registerViewModel.checkEmailExists(
                this,
                new RegisterUseCase(new RegisterRepositoryImpl()),
                new RegisterViewModel.OnCheckEmailExistsListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        warningEmail.setVisibility(View.GONE);
                        Intent intent = new Intent(TypeEmailActivity.this, SetPasswordActivity.class);
                        intent.putExtra("registerViewModel", registerViewModel);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        progressDialog.dismiss();
                        warningEmail.setText(error);
                        warningEmail.setVisibility(View.VISIBLE);
                    }
                }
        );
    }
    private void showErrorDialog(String message) {
        warningEmail.setVisibility(View.GONE);
        new AlertDialog.Builder(this)
                .setTitle("Lỗi kết nối")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}