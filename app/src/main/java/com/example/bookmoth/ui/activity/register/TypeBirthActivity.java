package com.example.bookmoth.ui.activity.register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Màn hình nhập ngày sinh cho quá trình đăng ký.
 * Người dùng có thể chọn ngày sinh bằng DatePickerDialog hoặc nhập thủ công.
 */
public class TypeBirthActivity extends AppCompatActivity {

    Button btnNext, iHaveAAccount, returnButton;
    TextView tvWarning;
    EditText edtDate;
    RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_type_birth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();

        clickPickDate();
        clickNext();
        clickIHaveAAccount();
        clickReturn();
    }

    /**
     * Khởi tạo các thành phần giao diện và lấy dữ liệu ViewModel từ Intent (nếu có).
     */
    private void init() {
        btnNext = findViewById(R.id.next_for_register);
        returnButton = findViewById(R.id.return_button);
        iHaveAAccount = findViewById(R.id.i_have_a_account);
        tvWarning = findViewById(R.id.tvWarning);
        edtDate = findViewById(R.id.edtDate);

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new ViewModelProvider(this).get(RegisterViewModel.class) :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");
    }


    /**
     * Xử lý sự kiện khi nhấn nút "Quay lại", đóng Activity hiện tại.
     */
    private void clickReturn() {
        returnButton.setOnClickListener(v -> finish());
    }

    /**
     * Xử lý sự kiện khi nhấn vào nút "Tôi đã có tài khoản", điều hướng đến màn hình đăng nhập.
     */
    private void clickIHaveAAccount() {
        iHaveAAccount.setOnClickListener(v -> {
            Intent intent = new Intent(TypeBirthActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tiếp theo", kiểm tra tính hợp lệ của ngày sinh và chuyển đến màn hình chọn giới tính.
     */
    private void clickNext() {
        btnNext.setOnClickListener(v -> {
            String ngaySinh = edtDate.getText().toString();
            String[] parts = ngaySinh.split(" ");
            if (parts.length == 4) {
                int dayOfMonth = Integer.parseInt(parts[0]);

                int _month = Integer.parseInt(parts[2].replace(",", ""));
                int _year = Integer.parseInt(parts[3]);
                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String strMonth = _month < 9 ? "0" + (_month + 1) : String.valueOf(_month + 1);

                ngaySinh = String.format("%s/%s/%s", strDay, strMonth, _year);
            }

            if (validateDateOfBirth(ngaySinh)) {
                tvWarning.setVisibility(View.GONE);
                registerViewModel.setDateOfBirth(ngaySinh);
                Intent intent = new Intent(TypeBirthActivity.this, TypeGenderActivity.class);
                intent.putExtra("registerViewModel", registerViewModel);
                startActivity(intent);
            } else {
                tvWarning.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào ô nhập ngày sinh, hiển thị DatePickerDialog.
     */
    private void clickPickDate() {
        edtDate.setOnClickListener(v -> {
            showDatePickerDialog();
        });
    }

    /**
     * Hiển thị DatePickerDialog để người dùng chọn ngày sinh.
     * Nếu ngày sinh đã có trong EditText, sử dụng ngày đó làm giá trị mặc định.
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        String dateText = edtDate.getText().toString().trim();
        if (!dateText.isEmpty()) {
            try {
                String[] parts = dateText.split(" tháng |, ");
                int selectedDay = Integer.parseInt(parts[0]);
                int selectedMonth = Integer.parseInt(parts[1]) - 1;
                int selectedYear = Integer.parseInt(parts[2]);

                calendar.set(selectedYear, selectedMonth, selectedDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Cập nhật TextView với ngày đã chọn
                    String selectedDate = selectedDay + " tháng " + (selectedMonth + 1) + ", " + selectedYear;
                    edtDate.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }


    /**
     * Kiểm tra ngày sinh có hợp lệ hay không.
     * Điều kiện:
     * - Ngày sinh phải nhỏ hơn ngày hiện tại.
     * - Tuổi phải từ 12 trở lên.
     *
     * @param dateOfBirth Ngày sinh ở định dạng "dd/MM/yyyy".
     * @return true nếu ngày sinh hợp lệ, ngược lại trả về false.
     */
    private boolean validateDateOfBirth(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        Calendar today = Calendar.getInstance();
        try {
            Date date = sdf.parse(dateOfBirth);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(date);

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return !date.after(new Date()) && age >= 12;
        } catch (ParseException e) {
            return false;
        }
    }

}