package com.example.bookmoth.ui.activity.login;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.login.LoginRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.databinding.ActivityLoginBinding;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.login.LoginUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.home.HomeActivity;
import com.example.bookmoth.ui.activity.register.OptionActivity;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.login.LoginViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Lớp LoginActivity chịu trách nhiệm xử lý giao diện đăng nhập của ứng dụng.
 * Hỗ trợ đăng nhập bằng email/mật khẩu và đăng nhập bằng tài khoản Google.
 */
public class LoginActivity extends AppCompatActivity {

    private Button loginWithEmail;
    private TextView forgotPassword, register;
    private LinearLayout loginWithGoogle;
    private LoginViewModel loginViewModel;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private TextInputEditText email, password;
    int RC_LOGIN = 20;
    private ActivityLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        new ProfileViewModel(new ProfileUseCase(
                new LocalProfileRepositoryImpl(
                        this, ProfileDatabase.getInstance(this).profileDao()),
                new ProfileRepositoryImpl()
        )).deleteProfile();

        forgotPassword = binding.forgotPasswordButton;
        register = binding.registerButton;
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
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

    /**
     * Thiết lập sự kiện cho nút "Đăng ký".
     * Khi người dùng nhấn vào nút này, màn hình đăng ký sẽ được mở ra.
     */
    private void clickRegister() {
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, OptionActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Thiết lập sự kiện cho nút "Quên mật khẩu".
     * Hiện tại phương thức này bị tạm thời vô hiệu hóa.
     */
    private void clickForgotPassword() {
//        forgotPassword.setOnClickListener(v -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
//        });
    }

    /**
     * Thiết lập sự kiện đăng nhập bằng email và mật khẩu.
     * Khi nhấn vào nút đăng nhập, phương thức sẽ lấy dữ liệu từ ô nhập liệu,
     * sau đó gửi yêu cầu xác thực qua `LoginViewModel`.
     */
    private void clickLoginWithEmail() {
        loginWithEmail.setOnClickListener(v -> {
            LoadingUtils.showLoading(getSupportFragmentManager());
            String mail = email.getText().toString();
            String pass = password.getText().toString();

            loginViewModel.login(this, mail, pass, new LoginViewModel.OnLoginListener() {
                @Override
                public void onSuccess() {
                    saveProfile();
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoadingUtils.hideLoading();
                    finish();
                }

                @Override
                public void onError(String error) {
                    LoadingUtils.hideLoading();
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * Thiết lập sự kiện đăng nhập bằng Google.
     * Nếu có kết nối Internet, phương thức sẽ thực hiện đăng nhập Google.
     * Nếu không có Internet, hiển thị thông báo lỗi.
     */
    private void clickLoginWithGoogle() {
        loginWithGoogle.setOnClickListener(view -> {
            if (InternetHelper.isNetworkAvailable(LoginActivity.this)) {
                LoadingUtils.showLoading(getSupportFragmentManager());
                Log.d("LoginActivity", "Network is available. Proceeding with Google sign-in.");
                loginWithGoogle();
            } else {
                Log.w("LoginActivity", "No Internet connection detected.");
                Toast.makeText(LoginActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Bắt đầu quá trình đăng nhập bằng Google.
     * Khởi chạy giao diện đăng nhập của Google, cho phép người dùng chọn tài khoản để đăng nhập.
     */
    private void loginWithGoogle() {
        Intent loginWithGoogleIntent = client.getSignInIntent();
        startActivityForResult(loginWithGoogleIntent, RC_LOGIN);
        LoadingUtils.hideLoading();
    }

    /**
     * Xử lý kết quả trả về từ Google Sign-In hoặc các Activity khác.
     *
     * @param requestCode Mã yêu cầu được gửi đi.
     * @param resultCode  Kết quả trả về từ Activity.
     * @param data        Dữ liệu trả về dưới dạng Intent.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_LOGIN) {
            LoadingUtils.showLoading(getSupportFragmentManager());
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                loginViewModel.loginWithGoogle(this, account.getIdToken(), new LoginViewModel.OnLoginListener() {
                    @Override
                    public void onSuccess() {
                        client.signOut();
                        saveProfile();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        LoadingUtils.hideLoading();
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        LoadingUtils.hideLoading();
                        client.signOut();
                    }
                });
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed. Code: " + e.getStatusCode(), e);
                LoadingUtils.hideLoading();
                handleGoogleSignInError(e);
            }
        }
    }

    /**
     * Lưu thông tin người dùng vào cơ sở dữ liệu.
     */
    private void saveProfile() {
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl()));

        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                if (profile == null) return;
                profileViewModel.saveProfile(profile);
            }

            @Override
            public void onProfileFailure(String error) {
            }
        });
    }

    /**
     * Xử lý các lỗi có thể xảy ra khi đăng nhập bằng Google.
     *
     * @param e Đối tượng `ApiException` chứa thông tin lỗi.
     */
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