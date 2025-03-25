package com.example.bookmoth.ui.activity.wallet;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.dialogs.PasswordPopup;

public class ConfirmActivity extends AppCompatActivity {

    private TextView txtAmount, txtBack;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        clickConfirm();
    }

    private void clickConfirm() {
        confirmButton.setOnClickListener(v -> {
            inputPin();
        });
    }

    private void inputPin() {
        PasswordPopup passwordPopup = new PasswordPopup(password -> {
            confirmPin(password);
        }, "Nhập mật khẩu để xác nhận giao dịch");

        passwordPopup.show(getSupportFragmentManager(), passwordPopup.getTag());
    }

    private void init() {
        confirmButton = findViewById(R.id.btn_confirm);
        txtAmount = findViewById(R.id.txtAmount);
        txtBack = findViewById(R.id.txtBack);
    }
    private void confirmPin(String password) {

    }
}