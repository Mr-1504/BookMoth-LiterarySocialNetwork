package com.example.bookmoth.ui.register;

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
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;


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

        returnButton = findViewById(R.id.return_button);
        iHaveAAccountButton = findViewById(R.id.i_have_a_account);
        nextButton = findViewById(R.id.next_for_register);
        warning = findViewById(R.id.warning);
        gender = findViewById(R.id.gender_group);

        registerViewModel = getIntent().getSerializableExtra("registerViewModel") == null ?
                new ViewModelProvider(this).get(RegisterViewModel.class) :
                (RegisterViewModel) getIntent().getSerializableExtra("registerViewModel");


        clickNext();
        clickIHaveAAccount();
        clickReturn();
    }

    public RegisterViewModel getSharedViewModel() {
        return registerViewModel;
    }

    private void clickReturn() {
        returnButton.setOnClickListener(view -> finish());
    }

    private void clickIHaveAAccount() {
        iHaveAAccountButton.setOnClickListener(view -> {
            Intent intent = new Intent(TypeGenderActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

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