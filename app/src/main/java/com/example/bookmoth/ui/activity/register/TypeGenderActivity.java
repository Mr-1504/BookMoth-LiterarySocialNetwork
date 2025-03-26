package com.example.bookmoth.ui.activity.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Gender;
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;

/**
 * Hoạt động (Activity) cho việc chọn giới tính trong quá trình đăng ký.
 * Người dùng có thể chọn giới tính của mình, sau đó chuyển sang bước tiếp theo.
 */
public class TypeGenderActivity extends AppCompatActivity {

    private Button returnButton, iHaveAAccountButton, nextButton;
    private TextView warning;
    private RadioGroup gender;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_gender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        clickNext();
        clickIHaveAAccount();
        clickReturn();
    }

    /**
     * Khởi tạo các thành phần giao diện và lấy dữ liệu từ Intent nếu có.
     */
    private void init() {
        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);
        nextButton = findViewById(R.id.next_for_register);
        warning = findViewById(R.id.warning);
        gender = findViewById(R.id.gender_group);

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new ViewModelProvider(this).get(RegisterViewModel.class) :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");

    }

    /**
     * Xử lý sự kiện khi nhấn nút "Quay lại".
     * Kết thúc Activity hiện tại và quay lại màn hình trước đó.
     */
    private void clickReturn() {
        returnButton.setOnClickListener(view -> finish());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tôi đã có tài khoản".
     * Chuyển hướng đến màn hình đăng nhập và xóa hết các Activity trước đó.
     */
    private void clickIHaveAAccount() {
        iHaveAAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(TypeGenderActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tiếp theo".
     * Nếu người dùng chưa chọn giới tính, hiển thị cảnh báo.
     * Nếu đã chọn, lưu giới tính vào ViewModel và chuyển sang màn hình nhập email.
     */
    private void clickNext() {
        nextButton.setOnClickListener(view -> {
            int selectedId = gender.getCheckedRadioButtonId();
            if (selectedId == -1) {
                warning.setVisibility(View.VISIBLE);
            } else {
                warning.setVisibility(View.GONE);
                Gender selectedGender = getSelectedGender(selectedId);
                registerViewModel.setGender(selectedGender);
                Log.d("GENDER_SELECTED", "Selected gender: " + selectedGender); // Kiểm tra xem có gọi không
                Intent intent = new Intent(TypeGenderActivity.this, TypeEmailActivity.class);
                intent.putExtra("registerViewModel", registerViewModel);
                startActivity(intent);
            }
        });
    }

    /**
     * Xác định giới tính dựa trên RadioButton được chọn.
     *
     * @param selectedId ID của RadioButton được chọn.
     * @return Giới tính tương ứng (`Gender.MALE`, `Gender.FEMALE`, hoặc `Gender.OTHER`).
     */
    private Gender getSelectedGender(int selectedId) {
        if (selectedId == R.id.radio_male) {
            return Gender.MALE;
        } else if (selectedId == R.id.radio_female) {
            return Gender.FEMALE;
        } else {
            return Gender.OTHER;
        }
    }

}