package com.example.bookmoth.ui.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.Extension;
import com.example.bookmoth.ui.dialogs.PasswordPopup;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;

public class DepositActivity extends AppCompatActivity {

    private int[] btnNumber;
    private Button btnBackspace, btnNext;
    private TextView tvAmount, tvBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deposit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        clickNumber();
        clickBackspace();
        clickNext();
    }

    private void clickNext() {
        btnNext.setOnClickListener(view -> {
            String amount = tvAmount.getText().toString();
            if (amount.equals("0đ")) {
                Toast.makeText(DepositActivity.this, R.string.please_input_amount, Toast.LENGTH_SHORT).show();
                return;
            }

            String nomalizedAmount = Extension.normalize(amount.substring(0, amount.length() - 1));
            if (Long.parseLong(nomalizedAmount) < 10000 ) {
                Toast.makeText(DepositActivity.this, R.string.mininum_requirement_10000, Toast.LENGTH_SHORT).show();
            }

            Intent intent = new Intent(this, ConfirmActivity.class);
            intent.putExtra("amount", amount);
        });
    }

    private void clickBackspace() {
        btnBackspace.setOnClickListener(view -> {
            String amount = tvAmount.getText().toString();
            if (!amount.isEmpty()) {
                if(amount.length() == 2) {
                    tvAmount.setText("0đ");
                    return;
                }
                amount = Extension.normalize(amount.substring(0, amount.length() - 2));
                tvAmount.setText(String.format("%sđ", Extension.fomatCurrency(amount)));
            }
        });
    }

    private void init() {
        btnNumber = new int[]{
                R.id.btn_num_0, R.id.btn_num_1, R.id.btn_num_2,
                R.id.btn_num_3, R.id.btn_num_4, R.id.btn_num_5,
                R.id.btn_num_6, R.id.btn_num_7, R.id.btn_num_8,
                R.id.btn_num_9, R.id.btn_num_000
        };

        btnNext = findViewById(R.id.btn_continue);
        btnBackspace = findViewById(R.id.btn_backspace);
        tvAmount = findViewById(R.id.tv_amount);
        tvBalance = findViewById(R.id.tv_balance);
        String balance = getIntent().getStringExtra("balance");
        String formattedBalance = String.format("%s %sđ", tvBalance.getText().toString(), balance);
        tvBalance.setText(formattedBalance);
    }

    private void clickNumber() {
        for (int i : btnNumber) {
            Button number = findViewById(i);
            number.setOnClickListener(v -> {
                String currentAmount = tvAmount.getText().toString();
                if(currentAmount.length() == 9) {
                    return;
                }
                if (currentAmount.equals("0đ")) {
                    tvAmount.setText(String.format("%sđ", number.getText().toString()));
                    return;
                }
                String amount = String.format(
                        "%s%s", Extension.normalize(currentAmount.substring(0, currentAmount.length() - 1)),
                        number.getText().toString());
                String newAmount = Extension.bigDecimalToAmount(BigDecimal.valueOf(Long.parseLong(amount)));
                tvAmount.setText(String.format("%sđ", newAmount));
            });
        }
    }
}