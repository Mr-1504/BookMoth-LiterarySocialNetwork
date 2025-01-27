package com.example.bookmoth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.utils.Api.CreateOrder;
import com.example.bookmoth.utils.Constant.AppInfo;
import com.example.bookmoth.utils.Helper.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PayActivity extends AppCompatActivity {

    private Button confirm;
    private TextView amount, price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
        confirm = findViewById(R.id.confirm);
        amount = findViewById(R.id.amount);
        price = findViewById(R.id.price);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String soluongString = amount.getText().toString().equals("") ? price.getText().toString() : amount.getText().toString();

                new Thread(() -> {
                    try {
                        JSONObject data = new CreateOrder().createOrder(soluongString, "");
                        String code = data.getString("returncode");
                        Log.i("Successed", "API Response: " + data.toString());

                        if (code.equals("1")) {
                            String token = data.optString("zptranstoken", null); // Lấy token an toàn
                            if (token != null) {
                                runOnUiThread(() -> startZaloPayPayment(token));
                            } else {
                                Log.e("Error", "Token is null");
                            }
                        } else {
                            Log.e("Error", "Invalid return code: " + code);
                        }
                    } catch (Exception e) {
                        Log.e("Error", "Error creating order", e);
                    }
                }).start();
            }
        });
    }

    private void startZaloPayPayment(String token) {
        ZaloPaySDK.getInstance().payOrder(PayActivity.this, token, "pay_order://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String s, String s1, String s2) {
                Log.i("PaymentCallback", "Payment succeeded with transaction: " + s);
                navigateToMainActivity("Thành công");
            }

            @Override
            public void onPaymentCanceled(String s, String s1) {
                Log.i("PaymentCallback", "Payment canceled");
                navigateToMainActivity("hủy");
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                Log.e("PaymentCallback", "Payment error: " + zaloPayError.toString());
                navigateToMainActivity("Thất bại");
            }

        });
    }

    private void navigateToMainActivity(String result) {
        Intent intent = new Intent(PayActivity.this, ResultAcitivity.class);
        intent.putExtra("res", result);
        startActivity(intent);
        finish();
    }


    @Override
    public void onNewIntent(Intent intent) {
        System.out.println("New Intent");
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

}