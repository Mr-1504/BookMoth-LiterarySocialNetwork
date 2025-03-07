package com.example.bookmoth.ui.register;

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

public class TypeNameActivity extends AppCompatActivity {

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
        EditText lastNameEditText = findViewById(R.id.last_name_for_register);
        EditText firstNameEditText = findViewById(R.id.firstname_for_register);
        TextView warningFullName = findViewById(R.id.warning_full_name);
        Button nextButton = findViewById(R.id.next_for_register);

        nextButton.setOnClickListener(view -> {
            String lastName = lastNameEditText.getText().toString();
            String firstName = firstNameEditText.getText().toString().trim();

            if (lastName.isEmpty() || firstName.isEmpty()) {
                warningFullName.setVisibility(View.VISIBLE);
            } else {
                warningFullName.setVisibility(View.GONE);
                // Xử lý tiếp tục nếu hợp lệ
            }
        });

    }
}