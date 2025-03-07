package com.example.bookmoth.ui.register;

import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TypeBirthActivity extends AppCompatActivity {

    Button btnNext;
    TextView tvWarning;
    EditText edtDate;
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

        btnNext = findViewById(R.id.next_for_register);
        tvWarning = findViewById(R.id.tvWarning);
        edtDate = findViewById(R.id.edtDate);
        edtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                edtDate.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }, year, month, day);

            datePickerDialog.show();
        });




        btnNext.setOnClickListener(v -> {
            String ngaySinh = edtDate.getText().toString();

            if (kiemTraNgaySinhHopLe(ngaySinh)) {
                tvWarning.setVisibility(View.GONE); // Ẩn cảnh báo nếu hợp lệ
            } else {
                tvWarning.setVisibility(View.VISIBLE); // Hiện cảnh báo nếu sai
            }
        });
    }


    private boolean kiemTraNgaySinhHopLe(String ngaySinh) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(ngaySinh);
            return !date.after(new Date()); // Ngày sinh không được là tương lai
        } catch (ParseException e) {
            return false; // Sai định dạng
        }
    }

}