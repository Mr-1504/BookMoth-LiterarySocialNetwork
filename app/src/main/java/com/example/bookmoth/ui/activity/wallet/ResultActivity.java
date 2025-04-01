package com.example.bookmoth.ui.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.enums.Transaction;

public class ResultActivity extends AppCompatActivity {

    private ImageView result;
    private TextView txtResult, txtAmount, txtTransId;
    private Button back;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        clickBack();
    }

    private void init(){
        result = findViewById(R.id.result);
        txtResult = findViewById(R.id.txtResult);
        txtAmount = findViewById(R.id.txt_amount);
        txtTransId = findViewById(R.id.transaction_id);
        back = findViewById(R.id.btn_close);

        int status = getIntent().getIntExtra("status", -1);
        Transaction.TransactionResult transactionResult = Transaction.getTransactionResult(status);
        String amount = getIntent().getStringExtra("amount");
        String transId = getIntent().getStringExtra("transId");

        txtTransId.setText(transId);
        txtAmount.setText(amount);

        switch (transactionResult){
            case SUCCESS:
                result.setImageDrawable(getDrawable(R.drawable.ic_done));
                txtResult.setText(getString(R.string.transaction_successfully));
                break;
            case FAILED:
                result.setImageDrawable(getDrawable(R.drawable.ic_failed));
                txtResult.setText(getString(R.string.transaction_failed));
                break;
            case CANCEL:
                result.setImageDrawable(getDrawable(R.drawable.ic_failed));
                txtResult.setText(getString(R.string.transaction_canceled));
                break;
            case ERROR:
                result.setImageDrawable(getDrawable(R.drawable.ic_failed));
                txtResult.setText(getString(R.string.transaction_error));
                break;
        }

         timer = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
                back.setText(getString(R.string.close) + "(" + millisUntilFinished / 1000 + "s)");
            }

            public void onFinish() {
                Intent intent = new Intent(ResultActivity.this, WalletActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void clickBack(){
        back.setOnClickListener(view -> {
            timer.cancel();
            Intent intent = new Intent(ResultActivity.this, WalletActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}