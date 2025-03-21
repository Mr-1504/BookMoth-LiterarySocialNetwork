package com.example.bookmoth.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;

public class WalletActivity extends AppCompatActivity {

    private ImageView btnDeposit, btnWithdraw, btnTransfer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wallet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnDeposit = findViewById(R.id.deposit);
        btnWithdraw = findViewById(R.id.withdraw);
        btnTransfer = findViewById(R.id.transfer);

        clickTransfer();
    }

    private void clickTransfer() {
        btnTransfer.setOnClickListener(v -> {
            Intent intent = new Intent(this, PayActivity.class);
            startActivity(intent);
        });
    }


}