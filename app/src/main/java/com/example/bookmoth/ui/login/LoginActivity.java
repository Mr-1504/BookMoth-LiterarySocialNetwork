package com.example.bookmoth.ui.login;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.login.LoginRepositoryImpl;
import com.example.bookmoth.databinding.ActivityLoginBinding;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.domain.usecase.login.LoginUseCase;
import com.example.bookmoth.ui.home.HomeActivity;
import com.example.bookmoth.ui.payment.PayActivity;
import com.example.bookmoth.ui.register.OptionActivity;
import com.example.bookmoth.ui.viewmodel.login.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private Button loginWithEmail;
    private TextView forgotPassword, register;
    private LinearLayout loginWithGoogle;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private TextInputEditText email, password;
    int RC_LOGIN = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        forgotPassword = binding.forgotPasswordButton;
        register = binding.registerButton;
        email = (TextInputEditText) binding.username;
        password = (TextInputEditText) binding.password;
        loginWithEmail = binding.loginWithEmail;
        loginWithGoogle = binding.loginWithGoogleButton;
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(LoginActivity.this, signInOptions);
        loginViewModel = new LoginViewModel(new LoginUseCase(new LoginRepositoryImpl()));

        clickLoginWithEmail();
        clickLoginWithGoogle();
        clickForgotPassword();
        clickRegister();
    }

    private void clickRegister() {
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, OptionActivity.class);
            startActivity(intent);
        });
    }

    private void clickForgotPassword() {
//        forgotPassword.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
//        });
    }

    private void clickLoginWithEmail() {
        loginWithEmail.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String pass = password.getText().toString();

            loginViewModel.login(this, mail, pass, new LoginViewModel.OnLoginListener() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(LoginActivity.this, PayActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    /**
     * Phương thức `clickLogin` dùng để thiết lập sự kiện khi người dùng nhấn nút đăng nhập với Google.<br>
     * - Trước khi thực hiện đăng nhập, phương thức sẽ kiểm tra kết nối Internet của thiết bị. <br>
     * - Nếu có kết nối Internet, phương thức sẽ gọi phương thức `loginWithGoogle()` để tiến hành đăng nhập. <br>
     * - Nếu không có kết nối Internet, phương thức sẽ hiển thị một thông báo cho người dùng biết rằng không có kết nối mạng.
     */
    private void clickLoginWithGoogle() {
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetHelper.isNetworkAvailable(LoginActivity.this)) {
                    Log.d("LoginActivity", "Network is available. Proceeding with Google sign-in.");
                    loginWithGoogle();
                } else {
                    Log.w("LoginActivity", "No Internet connection detected.");
                    Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Bắt đầu quá trình đăng nhập bằng Google.
     * <p>
     * Phương thức này sử dụng đối tượng GoogleSignInClient để tạo một intent,
     * từ đó khởi chạy giao diện đăng nhập của Google, cho phép người dùng chọn tài khoản Google
     * để xác thực. Kết quả của quá trình đăng nhập sẽ được trả về trong phương thức
     * {@link #onActivityResult(int, int, Intent)}.
     * <p>
     * Điều kiện tiên quyết:
     * - Đối tượng {@link com.google.android.gms.auth.api.signin.GoogleSignInClient} (client)
     * phải được khởi tạo đúng cách với các thông tin cần thiết (ví dụ: email, ID token, v.v.).
     * - Một mã yêu cầu hợp lệ (RC_LOGIN) phải được định nghĩa để xác định kết quả đăng nhập.
     * <p>
     * Ví dụ sử dụng:
     * <pre>
     * private static final int RC_LOGIN = 1001;
     *
     * private void loginWithGoogle() {
     *     Intent loginWithGoogleIntent = client.getSignInIntent();
     *     startActivityForResult(loginWithGoogleIntent, RC_LOGIN);
     * }
     * </pre>
     * <p>
     * Sau khi người dùng chọn tài khoản, kết quả sẽ được xử lý trong phương thức
     * {@link #onActivityResult(int, int, Intent)}.
     */
    private void loginWithGoogle() {
        Intent loginWithGoogleIntent = client.getSignInIntent();
        startActivityForResult(loginWithGoogleIntent, RC_LOGIN);
    }

    /**
     * Xử lý kết quả trả về từ các Activity khác. Hàm được gọi sau khi một Activity kết thúc và trả dữ liệu về.<br>
     *
     * @param requestCode Mã yêu cầu (request code) được gửi khi gọi startActivityForResult.
     *                    Dùng để xác định yêu cầu nào trả về kết quả.
     * @param resultCode  Mã kết quả (result code) trả về từ Activity, thường là RESULT_OK hoặc RESULT_CANCELED.
     * @param data        Dữ liệu trả về từ Activity dưới dạng Intent, có thể chứa các giá trị cần thiết.
     *                    <p>
     *                    Trong trường hợp này:<br>
     *                    - Hàm xử lý kết quả đăng nhập từ Google Sign-In.<br>
     *                    - Nếu `requestCode` trùng với `RC_SIGN_IN`, kiểm tra kết quả đăng nhập:<br>
     *                    - Lấy thông tin tài khoản Google từ `data` và thực hiện xác thực Firebase thông qua `firebaseAuth()`.<br>
     *                    - Nếu xảy ra lỗi, hiển thị thông báo lỗi và ghi log chi tiết.<br>
     *                    <p>
     *                    Các bước xử lý cụ thể:<br>
     *                    1. Kiểm tra `requestCode` để xác định yêu cầu nào trả về kết quả.<br>
     *                    2. Sử dụng `GoogleSignIn.getSignedInAccountFromIntent` để lấy thông tin tài khoản Google từ Intent.<br>
     *                    3. Nếu lấy thành công tài khoản, gọi hàm `firebaseAuth()` để xác thực với Firebase.<br>
     *                    4. Nếu xảy ra lỗi:<br>
     *                    - Hiển thị lỗi dưới dạng Toast cho người dùng.<br>
     *                    - Ghi log lỗi chi tiết để dễ dàng kiểm tra trong quá trình phát triển.<br>
     *                    <p>
     *                    Lưu ý:<br>
     *                    - `RC_SIGN_IN` là mã yêu cầu cho quá trình đăng nhập Google Sign-In.<br>
     *                    - `firebaseAuth()` là hàm xử lý xác thực người dùng với Firebase dựa trên mã thông báo (ID token) nhận được từ tài khoản Google.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                loginViewModel.loginWithGoogle(this, account.getIdToken(), new LoginViewModel.OnLoginListener() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(LoginActivity.this, PayActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, error , Toast.LENGTH_SHORT).show();
                        client.signOut();
                    }
                });
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed. Code: " + e.getStatusCode(), e);
                handleGoogleSignInError(e);
            }
        }
    }

    // Hàm xử lý lỗi chi tiết cho Google Sign-In
    private void handleGoogleSignInError(ApiException e) {
        int errorCode = e.getStatusCode();
        String errorMessage;
        switch (errorCode) {
            case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                errorMessage = getString(R.string.error_signin_cancelled);
                break;
            case GoogleSignInStatusCodes.NETWORK_ERROR:
                errorMessage = getString(R.string.error_network);
                break;
            case GoogleSignInStatusCodes.INVALID_ACCOUNT:
                errorMessage = getString(R.string.error_invalid_account);
                break;
            case GoogleSignInStatusCodes.API_NOT_CONNECTED:
                errorMessage = getString(R.string.api_not_connected);
                break;
            default:
                errorMessage = getString(R.string.error_unknown);
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}