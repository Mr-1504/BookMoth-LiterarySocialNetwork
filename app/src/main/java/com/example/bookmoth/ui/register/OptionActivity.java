package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.data.repository.register.RegisterRepositoryImpl;
import com.example.bookmoth.domain.usecase.register.RegisterUseCase;
import com.example.bookmoth.ui.viewmodel.register.RegisterViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;


/**
 * Lớp OptionActivity cung cấp các tùy chọn đăng ký tài khoản như đăng ký bằng email hoặc Google.
 */
public class OptionActivity extends AppCompatActivity {

    private static final int RC_REGISTER_WITH_GOOGLE = 100;
    private Button registerWithEmail, returnButton, ihavaAccount;
    private LinearLayout registerWithGoogle;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        clickRegisterWithEmail();
        clickRegisterWithGoogle();
        clickReturn();
        clickIHaveAAccount();
    }

    /**
     * Khởi tạo các thành phần UI, thiết lập Google Sign-In và ViewModel.
     */
    private void init() {
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .requestProfile()
                .requestScopes(new Scope("https://www.googleapis.com/auth/user.birthday.read"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/user.gender.read"))
                .build();
        client = GoogleSignIn.getClient(OptionActivity.this, signInOptions);

        registerWithEmail = findViewById(R.id.register_with_email);
        registerWithGoogle = findViewById(R.id.register_with_google);
        returnButton = findViewById(R.id.return_button);
        ihavaAccount = findViewById(R.id.i_have_a_account);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào nút "Tôi đã có tài khoản".
     * Đóng Activity hiện tại.
     */
    private void clickIHaveAAccount() {
        ihavaAccount.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Xử lý sự kiện khi người dùng nhấn vào nút "Trở về".
     * Đóng Activity hiện tại.
     */
    private void clickReturn() {
        returnButton.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Xử lý sự kiện khi người dùng chọn đăng ký bằng email.
     * Điều hướng đến màn hình nhập tên (TypeNameActivity).
     */
    private void clickRegisterWithEmail() {
        registerWithEmail.setOnClickListener(view -> {
            Intent intent = new Intent(OptionActivity.this, TypeNameActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Xử lý sự kiện khi người dùng chọn đăng ký bằng Google.
     * Kiểm tra kết nối internet trước khi thực hiện đăng nhập Google.
     */
    private void clickRegisterWithGoogle() {
        registerWithGoogle.setOnClickListener(view -> {
            if (InternetHelper.isNetworkAvailable(OptionActivity.this)) {
                Log.d("LoginActivity", "Network is available. Proceeding with Google sign-in.");
                registerWithGoogle();
            } else {
                Log.w("LoginActivity", "No Internet connection detected.");
                Toast.makeText(OptionActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Khởi động quá trình đăng nhập với Google.
     */
    private void registerWithGoogle() {
        Intent loginWithGoogleIntent = client.getSignInIntent();
        startActivityForResult(loginWithGoogleIntent, RC_REGISTER_WITH_GOOGLE);
    }

    /**
     * Xử lý kết quả trả về từ quá trình đăng nhập bằng Google.
     *
     * @param requestCode Mã yêu cầu để xác định kết quả trả về từ Google Sign-In.
     * @param resultCode  Mã kết quả của Activity (RESULT_OK hoặc RESULT_CANCELED).
     * @param data        Dữ liệu Intent chứa thông tin tài khoản Google.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_REGISTER_WITH_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                registerViewModel.registerWithGoogle(
                        this,
                        new RegisterUseCase(new RegisterRepositoryImpl()),
                        account.getIdToken(),
                        new RegisterViewModel.OnRegisterListener() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(OptionActivity.this, RegisterResultActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("registerViewModel", registerViewModel);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(OptionActivity.this, error, Toast.LENGTH_SHORT).show();
                                client.signOut();
                            }
                        }
                );
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed. Code: " + e.getStatusCode(), e);
                handleGoogleSignInError(e);
            }
        }
    }

    /**
     * Xử lý lỗi khi đăng nhập bằng Google thất bại.
     *
     * @param e Đối tượng ApiException chứa thông tin lỗi.
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