package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.login.LoginActivity;

public class TypeOtpActivity extends AppCompatActivity {

    private final EditText[] otpFields = new EditText[6];
    private Button btnSubmitOtp, returnButton, iHaveAAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        otpFields[0] = findViewById(R.id.otp1);
        otpFields[1] = findViewById(R.id.otp2);
        otpFields[2] = findViewById(R.id.otp3);
        otpFields[3] = findViewById(R.id.otp4);
        otpFields[4] = findViewById(R.id.otp5);
        otpFields[5] = findViewById(R.id.otp6);

        btnSubmitOtp = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);

        clickIHaveAAccount();
        clickReturnButton();
        setupOtpInputs();
        submitOTP();
    }

    private void clickIHaveAAccount() {
        iHaveAAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(TypeOtpActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void clickReturnButton() {
        returnButton.setOnClickListener(view -> finish());
    }

    private void submitOTP() {
        // Gọi hàm khi bấm nút xác nhận OTP
        btnSubmitOtp.setOnClickListener(v ->
        {
            String otp = getOtpCode();
            if (otp.length() == 6) {
                Toast.makeText(this, "OTP: " + otp, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Vui lòng nhập đủ 6 số!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setupOtpInputs() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus(); // Chuyển sang ô tiếp theo
                    }
                }


                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (index > 0) {
                        otpFields[index - 1].requestFocus(); // Quay lại ô trước khi xóa
                    }
                }
                return false;
            });
        }
    }

    private String getOtpCode() {
        StringBuilder otpCode = new StringBuilder();
        for (EditText otpField : otpFields) {
            otpCode.append(otpField.getText().toString());
        }
        return otpCode.toString();
    }


}