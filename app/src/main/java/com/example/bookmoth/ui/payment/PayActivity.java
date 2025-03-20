package com.example.bookmoth.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.Api.CreateOrder;
import com.example.bookmoth.core.utils.Constant.AppInfo;
import com.example.bookmoth.ui.viewmodel.payment.PaymentViewModel;

import org.json.JSONObject;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PayActivity extends AppCompatActivity {

    private Button confirm;
    private TextView amount, price;
    private PaymentViewModel paymentViewModel;

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
        paymentViewModel = new PaymentViewModel();
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);
        confirm = findViewById(R.id.confirm);
        amount = findViewById(R.id.amount);
        price = findViewById(R.id.price);

        confirm.setOnClickListener(view -> {
            String soluongString = amount.getText().toString().equals("") ? price.getText().toString() : amount.getText().toString();
            long soluong = Long.parseLong(soluongString);
            paymentViewModel.createOrder(this, soluong,
                    "MUA_KIM_CUONG_FREE_FIRE", true,
                    new PaymentViewModel.OnCreateOrderListener() {
                        @Override
                        public void onCreateOrderSuccess(String zaloPayTransToken) {
                            startZaloPayPayment(zaloPayTransToken);
                        }

                        @Override
                        public void onCreateOrderFailure(String message) {
                            Toast.makeText(PayActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );

//            new Thread(() -> {
//                try {
//                    JSONObject data = new CreateOrder().createOrder(soluongString, "Minh", "MUA_KIM_CUONG_FREE_FIRE");
//                    String code = data.getString("returncode");
//                    Log.i("Successed", "API Response: " + data);
//
//                    if (code.equals("1")) {
//                        String token = data.optString("zptranstoken", null);
//                        if (token != null) {
//                            runOnUiThread(() -> startZaloPayPayment(token));
//                        } else {
//                            Log.e("Error", "Token is null");
//                        }
//                    } else {
//                        Log.e("Error", "Invalid return code: " + code);
//                    }
//                } catch (Exception e) {
//                    Log.e("Error", "Error creating order", e);
//                }
//            }).start();
        });
    }

    private void startZaloPayPayment(String token) {
        ZaloPaySDK.getInstance().payOrder(PayActivity.this, token, "pay_order://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String s, String s1, String s2) {
                Log.i("PaymentCallback", "Payment succeeded with transaction: " + s);
                Toast.makeText(PayActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaymentCanceled(String s, String s1) {
                Log.i("PaymentCallback", "Payment canceled");
                Toast.makeText(PayActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                Log.e("PaymentCallback", "Payment error: " + zaloPayError.toString());
                Toast.makeText(PayActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
            }

        });
    }


    @Override
    public void onNewIntent(Intent intent) {
        System.out.println("New Intent");
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

}