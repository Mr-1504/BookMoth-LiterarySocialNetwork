package com.example.bookmoth.ui.activity.register;

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
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

/**
 * Hoạt động (Activity) cho việc nhập email trong quá trình đăng ký tài khoản.
 * Kiểm tra hợp lệ của email và xác minh xem email đã tồn tại hay chưa trước khi chuyển tiếp.
 */
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

        init();

        clickNext();
        clickReturn();
        clickIHaveAAccount();
    }

    /**
     * Khởi tạo các thành phần giao diện và lấy dữ liệu từ Intent nếu có.
     */
    private void init() {
        email = findViewById(R.id.edtEmail);
        warningEmail = findViewById(R.id.tvWarning);
        nextButton = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new ViewModelProvider(this).get(RegisterViewModel.class) :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");
    }


    /**
     * Xử lý sự kiện khi nhấn nút "Quay lại".
     * Kết thúc Activity hiện tại và quay lại màn hình trước đó.
     */
    private void clickReturn() {
        returnButton.setOnClickListener(v -> finish());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tôi đã có tài khoản".
     * Chuyển hướng đến màn hình đăng nhập và xóa hết các Activity trước đó.
     */
    private void clickIHaveAAccount() {
        iHaveAAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tiếp theo".
     * Kiểm tra email hợp lệ trước khi gửi yêu cầu kiểm tra email đã tồn tại.
     */
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

    /**
     * Kiểm tra email có tồn tại trong hệ thống hay không.
     *
     * @param email Địa chỉ email cần kiểm tra.
     */
    private void checkEmail(String email) {
        LoadingUtils.showLoading(getSupportFragmentManager());

        registerViewModel.setEmail(email);
        registerViewModel.checkEmailExists(
                this,
                new RegisterUseCase(new RegisterRepositoryImpl()),
                new RegisterViewModel.OnCheckEmailExistsListener() {
                    @Override
                    public void onSuccess() {
                        LoadingUtils.hideLoading();
                        warningEmail.setVisibility(View.GONE);
                        Intent intent = new Intent(TypeEmailActivity.this, SetPasswordActivity.class);
                        intent.putExtra("registerViewModel", registerViewModel);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(String error) {
                        LoadingUtils.hideLoading();
                        showErrorDialog(error);
                    }
                }
        );
    }

    /**
     * Hiển thị hộp thoại lỗi khi có vấn đề về kết nối hoặc lỗi hệ thống.
     *
     * @param message Nội dung lỗi cần hiển thị.
     */
    private void showErrorDialog(String message) {
        warningEmail.setVisibility(View.GONE);
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}