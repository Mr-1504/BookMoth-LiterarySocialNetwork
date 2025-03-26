package com.example.bookmoth.ui.activity.wallet;

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
import com.example.bookmoth.core.utils.Constant.AppInfo;
import com.example.bookmoth.core.utils.Extension;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.data.repository.wallet.WalletRepositoryImpl;
import com.example.bookmoth.domain.model.payment.TransactionType;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.dialogs.PasswordPopup;
import com.example.bookmoth.ui.viewmodel.payment.PaymentViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;

import java.text.Normalizer;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmActivity extends AppCompatActivity {

    private TextView txtAmount, txtDescription, txtBack;
    private Button confirmButton;
    private PasswordPopup passwordPopup;
    private WalletViewModel walletViewModel;
    private PaymentViewModel paymentViewModel;
    private ProfileViewModel profileViewModel;
    private Profile _profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        String amount = getIntent().getStringExtra("amount");
        init(amount);
        clickConfirm();
        clickBack();
    }

    private void clickBack() {
        txtBack.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Xử lý sự kiện khi click vào nút xác nhận.
     */
    private void clickConfirm() {
        confirmButton.setOnClickListener(v -> {
            inputPin();
        });
    }

    /**
     * Hiển thị popup nhập mã PIN.
     */
    private void inputPin() {
        passwordPopup = new PasswordPopup(password -> {
            confirmPin(password);
        }, "Nhập mật khẩu để xác nhận giao dịch");

        passwordPopup.show(getSupportFragmentManager(), passwordPopup.getTag());
    }

    /**
     * Khởi tạo các thành phần giao diện và ViewModel.
     */
    private void init(String amount) {
        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));
        paymentViewModel = new PaymentViewModel();
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
        );

        profileViewModel.getProfileLocal(new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                _profile = profile;
            }

            @Override
            public void onProfileFailure(String error) {

            }
        });
        confirmButton = findViewById(R.id.btn_confirm);
        txtAmount = findViewById(R.id.txtAmount);
        txtAmount.setText(amount);
        txtBack = findViewById(R.id.txtBack);
        txtDescription = findViewById(R.id.tvDescription);
    }

    /**
     * Xác nhận mã PIN của người dùng.
     * Nếu mã PIN đúng, tiếp tục thực hiện giao dịch.
     *
     * @param password Mật khẩu người dùng nhập vào.
     */
    private void confirmPin(String password) {
        LoadingUtils.showLoading(getSupportFragmentManager());
        walletViewModel.confirmPin(this, password, new WalletViewModel.OnWalletListener() {
            @Override
            public void onSuccess(BalanceResponse balanceResponse) {
                passwordPopup.dismiss();
                createOrder();
            }

            @Override
            public void onFailed(String error) {
                LoadingUtils.hideLoading();
                passwordPopup.setErrorMessage(error);
            }
        });
    }


    private void createOrder(){
        String stringAmount = txtAmount.getText().toString();
        long amount = Long.parseLong(Extension.normalize(
                stringAmount.substring(0, stringAmount.length() - 1)));
        String fullname = Normalizer.normalize(
                _profile.getLastName() + " " + _profile.getFirstName(), Normalizer.Form.NFD);
        String description = String.format(
                "%s %s", fullname, txtDescription.getText().toString());
        paymentViewModel.createOrder(
                ConfirmActivity.this,
                amount, description,
                TransactionType.DEPOSIT,
                new PaymentViewModel.OnCreateOrderListener() {
                    @Override
                    public void onCreateOrderSuccess(ZaloPayTokenResponse token) {
                        LoadingUtils.hideLoading();
                        startZaloPayPayment(token);
                    }

                    @Override
                    public void onCreateOrderFailure(String message) {
                        LoadingUtils.hideLoading();
                        Toast.makeText(ConfirmActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startZaloPayPayment(ZaloPayTokenResponse token) {
        String stringAmount = txtAmount.getText().toString();
        ZaloPaySDK.getInstance().payOrder(ConfirmActivity.this, token.getZaloToken(), "pay_order://app", new PayOrderListener() {
            @Override
            public void onPaymentSucceeded(String s, String s1, String s2) {
                Log.i("PaymentCallback", "Payment succeeded with transaction: " + s);
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("status", 1);
                intent.putExtra("amount", stringAmount);
                intent.putExtra("transId", token.getTransId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onPaymentCanceled(String s, String s1) {
                Log.i("PaymentCallback", "Payment canceled");
                Intent intent = new Intent(ConfirmActivity.this, ResultActivity.class);
                intent.putExtra("status", -1);
                intent.putExtra("amount", stringAmount);
                intent.putExtra("transId", token.getTransId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                Log.e("PaymentCallback", "Payment error: " + zaloPayError.toString());
                Intent intent = new Intent(ConfirmActivity.this, ResultActivity.class);
                intent.putExtra("status", 0);
                intent.putExtra("amount", stringAmount);
                intent.putExtra("transId", token.getTransId());
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * Xử lý Intent mới được gửi đến Activity (sử dụng cho kết quả thanh toán từ ZaloPay).
     *
     * @param intent Đối tượng Intent chứa dữ liệu phản hồi từ ZaloPay.
     */
    @Override
    public void onNewIntent(Intent intent) {
        System.out.println("New Intent");
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}