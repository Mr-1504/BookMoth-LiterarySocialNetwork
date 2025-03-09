package com.example.bookmoth.ui.register;

import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TypeBirthActivity extends AppCompatActivity {

    Button btnNext, iHaveAAccount, returnButton;
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
        returnButton = findViewById(R.id.return_button);
        iHaveAAccount = findViewById(R.id.i_have_a_account);
        tvWarning = findViewById(R.id.tvWarning);
        edtDate = findViewById(R.id.edtDate);

        clickPickDate();
        clickNext();
        clickIHaveAAccount();
        clickReturn();
    }

    private void clickReturn() {
        returnButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void clickIHaveAAccount() {
        iHaveAAccount.setOnClickListener(v -> {
            Intent intent = new Intent(TypeBirthActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void clickNext() {
        btnNext.setOnClickListener(v -> {
            String ngaySinh = edtDate.getText().toString();

            if (validateDateOfBirth(ngaySinh)) {
                tvWarning.setVisibility(View.GONE);
                Intent intent = new Intent(TypeBirthActivity.this, TypeGenderActivity.class);
                startActivity(intent);
            } else {
                tvWarning.setVisibility(View.VISIBLE);
            }
        });
    }

    private void clickPickDate() {
        edtDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                String strMonth = month1 < 9 ? "0" + (month1 + 1) : String.valueOf(month1 + 1);

                edtDate.setText(strDay + "/" + strMonth + "/" + year1);
            }, year, month, day);

            datePickerDialog.show();
        });
    }


    private boolean validateDateOfBirth(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(dateOfBirth);
            return !date.after(new Date()); // Ngày sinh không được là tương lai
        } catch (ParseException e) {
            return false; // Sai định dạng
        }
    }

}