package com.example.bookmoth.ui.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.BuildConfig;
import com.example.bookmoth.R;
import com.example.bookmoth.core.enums.PaymentMethod;
import com.example.bookmoth.core.enums.Transaction;
import com.example.bookmoth.core.utils.Constant.AppInfo;
import com.example.bookmoth.core.utils.Extension;
import com.example.bookmoth.core.utils.HMacHelper;
import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.data.repository.wallet.WalletRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.model.wallet.OrderWorkResponse;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.dialogs.PasswordPopup;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;

import java.text.Normalizer;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmActivity extends AppCompatActivity {

    private TextView txtAmount;
    private TextView txtDescription;
    private TextView txtBack;
    private TextView txtTransactionId;
    private Button confirmButton;
    private LinearLayout btnZaloPay, btnOption;
    private PasswordPopup passwordPopup;
    private WalletViewModel walletViewModel;
    private PaymentMethod.Payment_Method paymentMethod;
    private Transaction.TransactionType transactionType;
    private ZaloPayTokenResponse token;
    private String appTransId;
    private Profile me;

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


        init();
        clickConfirm();
        clickBack();
        clickZaloPay();
        clickWallet();
    }

    private void clickWallet() {
        btnOption.setOnClickListener(view -> {
            paymentMethod = PaymentMethod.Payment_Method.Wallet;
            btnOption.setBackgroundResource(R.drawable.button_selector);
            btnZaloPay.setBackgroundResource(R.drawable.button);
        });
    }

    private void clickZaloPay() {
        btnZaloPay.setOnClickListener(view -> {
            paymentMethod = PaymentMethod.Payment_Method.ZaloPay;
            btnZaloPay.setBackgroundResource(R.drawable.button_selector);
            btnOption.setBackgroundResource(R.drawable.button);
        });
    }

    private void clickBack() {
        txtBack.setOnClickListener(v -> finish());
    }

    /**
     * Xử lý sự kiện khi click vào nút xác nhận.
     */
    private void clickConfirm() {
        confirmButton.setOnClickListener(v -> inputPin());
    }

    /**
     * Hiển thị popup nhập mã PIN.
     */
    private void inputPin() {
        passwordPopup = new PasswordPopup(this::confirmPin, "Nhập mật khẩu để xác nhận giao dịch");

        passwordPopup.show(getSupportFragmentManager(), passwordPopup.getTag());
    }

    /**
     * Khởi tạo các thành phần giao diện và ViewModel.
     */
    private void init() {
        LoadingUtils.showLoading(getSupportFragmentManager());
        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));

        Intent infor = getIntent();

        int type = infor.getIntExtra("type", 0);
        transactionType = Transaction.getTransactionType(type);

        confirmButton = findViewById(R.id.btn_confirm);
        txtAmount = findViewById(R.id.txtAmount);
        txtBack = findViewById(R.id.txtBack);
        txtDescription = findViewById(R.id.tvDescription);
        TextView tvOption = findViewById(R.id.tvOption);
        ImageView ivOption = findViewById(R.id.ivOption);
        btnZaloPay = findViewById(R.id.btnZaloPay);
        btnOption = findViewById(R.id.btnOption);
        txtTransactionId = findViewById(R.id.transaction_id);


        switch (transactionType) {
            case DEPOSIT:
                tvOption.setText(getString(R.string.vietinbank));
                ivOption.setImageResource(R.drawable.ic_vietinbank);
                String amount = infor.getStringExtra("amount");
                me = (Profile) infor.getSerializableExtra("me");
                createOrder(amount);
                break;
            case PAYMENT:
                tvOption.setText(getString(R.string.wallet));
                ivOption.setImageResource(R.drawable.ic_app);
                int workId = infor.getIntExtra("workId", 0);
                if (workId == 0) {
                    showError();
                }
                orderProduct(workId);
                break;
        }
        paymentMethod = PaymentMethod.Payment_Method.ZaloPay;
    }

    /**
     * Tạo giao dịch mua sản phẩm.
     *
     * @param workId ID công việc cần mua.
     */
    private void orderProduct(int workId) {
        String time = String.valueOf(Extension.getTimeStamp());
        String data = workId + "|" + time;
        String mac = HMacHelper.computeHmac(BuildConfig.MAC_KEY, data);

        walletViewModel.orderProduct(this, workId, time, mac, new WalletViewModel.OnOrderProductListener() {
            @Override
            public void onSuccess(OrderWorkResponse response) {
                LoadingUtils.hideLoading();
                appTransId = response.getTransId();
                txtDescription.setText(response.getDesc());
                txtAmount.setText(Extension.bigDecimalToAmount(response.getAmount()));
                txtTransactionId.setText(response.getTransId());
            }

            @Override
            public void onFailed(String error) {
                LoadingUtils.hideLoading();
                if ("INVALID_WALLET".equals(error)) {
                    Toast.makeText(
                            ConfirmActivity.this, getString(R.string.please_create_wallet), Toast.LENGTH_SHORT).show();
                    finish();
                } else if ("409".equals(error)) {
                    Toast.makeText(
                            ConfirmActivity.this, getString(R.string.has_owned), Toast.LENGTH_SHORT).show();
                    finish();
                } else
                    showError();
            }
        });
    }

    /**
     * Hiển thị thông báo lỗi khi giao dịch thất bại.
     */
    private void showError() {
        Intent intent = new Intent(ConfirmActivity.this, ResultActivity.class);
        intent.putExtra("status",
                Transaction.getTransactionResult(Transaction.TransactionResult.FAILED));
        intent.putExtra("transId", appTransId);
        intent.putExtra("amount", txtAmount.getText().toString());
        startActivity(intent);
        finish();
    }


    private void updatePaymentMethod() {
        walletViewModel.updatePaymentMethod(this, appTransId, paymentMethod,
                new WalletViewModel.OnUpdatePaymentMethodListener() {
                    @Override
                    public void onUpdatePaymentMethodSuccess() {
                        switch (paymentMethod) {
                            case Wallet:
                                paymentWithWallet();
                                break;
                            case ZaloPay:
                                if (transactionType == Transaction.TransactionType.DEPOSIT){
                                    startZaloPayPayment(token);
                                } else {
                                    createZaloPayOrder();
                                }
                                break;
                        }
                    }

                    @Override
                    public void onUpdatePaymentMethodFailure(String error) {
                        LoadingUtils.hideLoading();
                        if ("INVALID_WALLET".equals(error)) {
                            Toast.makeText(
                                    ConfirmActivity.this, getString(R.string.please_create_wallet), Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            showError();
                    }
                });
    }

    private void createZaloPayOrder(){
        walletViewModel.createZaloPayOrder(this, appTransId, new WalletViewModel.OnCreateOrderListener() {
            @Override
            public void onCreateOrderSuccess(ZaloPayTokenResponse response) {
                token = response;
                LoadingUtils.hideLoading();
                startZaloPayPayment(token);
            }

            @Override
            public void onCreateOrderFailure(String message) {
                LoadingUtils.hideLoading();
                showError();
            }
        });
    }


    private void paymentWithWallet() {
        walletViewModel.paymentWithWallet(this, appTransId, new WalletViewModel.OnPaymentListener() {
            @Override
            public void onSuccess() {
                LoadingUtils.hideLoading();
                Intent intent = new Intent(ConfirmActivity.this, ResultActivity.class);
                intent.putExtra("status",
                        Transaction.getTransactionResult(Transaction.TransactionResult.SUCCESS));
                intent.putExtra("transId", appTransId);
                intent.putExtra("amount", txtAmount.getText().toString());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(String error) {
                LoadingUtils.hideLoading();
                if ("INVALID_WALLET".equals(error)) {
                    Toast.makeText(
                            ConfirmActivity.this, getString(R.string.please_create_wallet), Toast.LENGTH_SHORT).show();
                    finish();
                } else if ("INVALID_RECEIVER_WALLET".equals(error)) {
                    Toast.makeText(
                            ConfirmActivity.this, getString(R.string.invalid_receiver), Toast.LENGTH_SHORT).show();
                } else if ("INSUFFICIENT_FUNDS".equals(error)) {
                    Toast.makeText(
                            ConfirmActivity.this, getString(R.string.insufficient_funds), Toast.LENGTH_SHORT).show();
                } else
                    showError();
            }
        });
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
                updatePaymentMethod();
            }

            @Override
            public void onFailed(String error) {
                LoadingUtils.hideLoading();
                passwordPopup.setErrorMessage(error);
            }
        });
    }


    private void createOrder(String amount) {
        String fullname = Normalizer.normalize(
                me.getLastName() + " " + me.getFirstName(), Normalizer.Form.NFD);

        String description = String.format(
                "%s %s", fullname, getString(R.string.deposit_into_bookmoth_account));

        walletViewModel.createOrder(
                ConfirmActivity.this, Long.parseLong(amount), description,
                Transaction.TransactionType.DEPOSIT, new WalletViewModel.OnCreateOrderListener() {
                    @Override
                    public void onCreateOrderSuccess(ZaloPayTokenResponse response) {
                        LoadingUtils.hideLoading();
                        token = response;
                        appTransId = response.getTransId();
                        txtDescription.setText(description);
                        txtAmount.setText(Extension.fomatCurrency(amount));
                        txtTransactionId.setText(response.getTransId());
                    }

                    @Override
                    public void onCreateOrderFailure(String message) {
                        LoadingUtils.hideLoading();
                        showError();
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
    public void onNewIntent(@NonNull Intent intent) {
        System.out.println("New Intent");
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}