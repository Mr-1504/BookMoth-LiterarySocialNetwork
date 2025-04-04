package com.example.bookmoth.ui.activity.register;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity cho phép người dùng nhập họ và tên khi đăng ký.
 */
public class TypeNameActivity extends AppCompatActivity {

    private Button returnButton, iHaveAAccountButton, nextButton;
    private TextInputEditText lastNameEditText, firstNameEditText;
    private TextView warningFullName;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_typename);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        // Xử lý sự kiện click
        clickNext();
        clickReturnButton();
        clickIHaveAAccountButton();
    }

    /**
     * Khởi tạo các thành phần UI và ViewModel.
     */
    private void init() {
        lastNameEditText = findViewById(R.id.last_name_for_register);
        firstNameEditText = findViewById(R.id.firstname_for_register);
        warningFullName = findViewById(R.id.warning_full_name);
        nextButton = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Tiếp theo".
     * Kiểm tra đầu vào hợp lệ trước khi chuyển sang màn hình tiếp theo.
     */
    private void clickNext() {
        nextButton.setOnClickListener(view -> {
            String lastName = lastNameEditText.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();

            if (lastName.length() < 2 || firstName.length() < 2) {
                warningFullName.setVisibility(View.VISIBLE);
            } else {
                warningFullName.setVisibility(View.GONE);

                // Lưu dữ liệu vào ViewModel
                registerViewModel.setFirstName(firstName);
                registerViewModel.setLastName(lastName);

                // Chuyển sang màn hình tiếp theo
                Intent intent = new Intent(TypeNameActivity.this, TypeBirthActivity.class);
                intent.putExtra("registerViewModel", registerViewModel);
                startActivity(intent);
            }
        });
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Tôi đã có tài khoản".
     * Chuyển đến màn hình đăng nhập và xóa hết các Activity trước đó.
     */
    private void clickIHaveAAccountButton() {
        iHaveAAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(TypeNameActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn nút "Trở về".
     * Đóng Activity hiện tại.
     */
    private void clickReturnButton() {
        returnButton.setOnClickListener(view -> {
            finish();
        });
    }
}
