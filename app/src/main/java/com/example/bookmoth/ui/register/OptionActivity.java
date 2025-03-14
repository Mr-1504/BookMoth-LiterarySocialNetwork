package com.example.bookmoth.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.bookmoth.ui.home.MainActivity;
import com.example.bookmoth.ui.login.LoginActivity;
import com.example.bookmoth.ui.viewmodel.LoginViewModel;
import com.example.bookmoth.ui.viewmodel.registerViewModel.RegisterViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class OptionActivity extends AppCompatActivity {

    private static final int RC_REGISTER_WITH_GOOGLE = 100;
    private Button registerWithEmail;
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

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(OptionActivity.this, signInOptions);

        registerWithEmail = findViewById(R.id.register_with_email);
        registerWithGoogle = findViewById(R.id.register_with_google);


        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        clickRegisterWithEmail();
        clickRegisterWithGoogle();
    }

    private void clickRegisterWithEmail() {
    }

    private void clickRegisterWithGoogle() {
        registerWithGoogle.setOnClickListener(view -> {
            if (InternetHelper.isNetworkAvailable(OptionActivity.this)) {
                Log.d("LoginActivity", "Network is available. Proceeding with Google sign-in.");
                loginWithGoogle();
            } else {
                Log.w("LoginActivity", "No Internet connection detected.");
                Toast.makeText(OptionActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
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
        startActivityForResult(loginWithGoogleIntent, RC_REGISTER_WITH_GOOGLE);
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

        if (requestCode == RC_REGISTER_WITH_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                String id = account.getIdToken();
                registerViewModel.registerWithGoogle(
                        this,
                        new RegisterUseCase(new RegisterRepositoryImpl()),
                        account.getIdToken(),
                        new RegisterViewModel.OnRegisterListener() {
                            @Override
                            public void onSuccess() {
                                Intent intent = new Intent(OptionActivity.this, RegisterResultActivity.class);
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