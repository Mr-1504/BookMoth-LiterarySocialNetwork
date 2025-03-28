package com.example.bookmoth.ui.activity.register;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.register.RegisterRepositoryImpl;
import com.example.bookmoth.domain.usecase.register.RegisterUseCase;
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;

/**
 *
 */
public class TypeOtpActivity extends AppCompatActivity {

    private final EditText[] otpFields = new EditText[6];
    private Button btnSubmitOtp, returnButton, iHaveAAccountButton;
    private RegisterViewModel registerViewModel;
    private TextView tvResendOtp, tvWarning;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int secondResend = 30;

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

        tvResendOtp = findViewById(R.id.resend_otp);
        tvWarning = findViewById(R.id.tvWarning);

        btnSubmitOtp = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new RegisterViewModel() :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");

        clickIHaveAAccount();
        clickReturnButton();
        setupOtpInputs();
        submitOTP();
        setCountdownResendOtp();
        clickResend();
    }

    private void clickResend() {
        tvResendOtp.setOnClickListener(v -> {
            LoadingUtils.showLoading(getSupportFragmentManager());
            if (secondResend < 0) {
                registerViewModel.getOtp(this, new RegisterUseCase(new RegisterRepositoryImpl()), new RegisterViewModel.OnGetOtpListener() {
                    @Override
                    public void onSuccess() {
                        LoadingUtils.hideLoading();
                        secondResend = 30;
                        setCountdownResendOtp();
                        tvWarning.setVisibility(View.GONE);
                        for (EditText et : otpFields) {
                            et.setText("");
                        }
                        otpFields[0].requestFocus();
                    }

                    @Override
                    public void onError(String error) {
                        LoadingUtils.hideLoading();
                        showErrorDialog(error);
                        tvWarning.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(TypeOtpActivity.this, "Vui lòng đợi " + secondResend + " giây để gửi lại mã OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCountdownResendOtp() {
        String time = getString(R.string.resend_otp_after);
        Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (secondResend >= 0) {
                    tvResendOtp.setText(time + (secondResend < 10 ? "0" + secondResend : secondResend));
                    secondResend--;

                    if (secondResend >= 0) {
                        handler.postDelayed(this, 1000);
                    } else {
                        tvResendOtp.setText(R.string.resend_otp_now);
                    }
                }
            }
        };
        handler.post(countdownRunnable);
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
                verifyOtp(otp);
            } else {
                reset();
                tvWarning.setText(getString(R.string.isEmptyOtp));
                tvWarning.setVisibility(View.VISIBLE);
            }
        });

    }

    private void verifyOtp(String otp) {
        LoadingUtils.showLoading(getSupportFragmentManager());
        registerViewModel.setOtp(otp);
        registerViewModel.verifyOtp(
                this,
                new RegisterUseCase(new RegisterRepositoryImpl()),
                new RegisterViewModel.OnVerifyOtpListener() {
                    @Override
                    public void onSuccess() {
                        LoadingUtils.hideLoading();
                        tvWarning.setVisibility(View.GONE);
                        register();
                    }

                    @Override
                    public void onError(String error) {
                        LoadingUtils.hideLoading();
                        tvWarning.setText(error);
                        tvWarning.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    private void register() {
        LoadingUtils.showLoading(getSupportFragmentManager());
        registerViewModel.register(
                this,
                new RegisterUseCase(new RegisterRepositoryImpl()),
                new RegisterViewModel.OnRegisterListener() {
                    @Override
                    public void onSuccess() {
                        LoadingUtils.hideLoading();
                        tvWarning.setVisibility(View.GONE);
                        Intent intent = new Intent(TypeOtpActivity.this, RegisterResultActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        LoadingUtils.hideLoading();
                        showErrorDialog(error);
                        Intent intent = new Intent(TypeOtpActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
        );
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
                        otpFields[index + 1].requestFocus();
                    }
                }


                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (index > 0 && otpFields[index].getText().toString().isEmpty()) {
                        otpFields[index - 1].setText("");
                        otpFields[index - 1].requestFocus();
                    }
                }
                return false;
            });
        }
    }

    private void reset(){
        for(int i = 0; i < otpFields.length; i++){
            otpFields[i].setText("");
        }
        otpFields[0].requestFocus();
    }

    private String getOtpCode() {
        StringBuilder otpCode = new StringBuilder();
        for (EditText otpField : otpFields) {
            otpCode.append(otpField.getText().toString());
        }
        return otpCode.toString();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}