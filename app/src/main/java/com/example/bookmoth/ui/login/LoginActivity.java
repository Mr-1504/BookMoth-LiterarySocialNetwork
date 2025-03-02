package com.example.bookmoth.ui.login;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmoth.LogoutActivity;
import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.login.LoginRepositoryImpl;
import com.example.bookmoth.databinding.ActivityLoginBinding;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.domain.usecase.login.LoginUseCase;
import com.example.bookmoth.ui.home.MainActivity;
import com.example.bookmoth.ui.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Button loginWithGoogle, loginWithEmail;
    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private FirebaseDatabase database;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private FirebaseAuth firebaseAuth;
    private EditText email, password;
    int RC_LOGIN = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        email = binding.username;
        password = binding.password;
        loginWithEmail = binding.login;
        loginWithGoogle = binding.loginWithGoogleButton;
        database = FirebaseDatabase.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(LoginActivity.this, signInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
        loginViewModel = new LoginViewModel(new LoginUseCase(new LoginRepositoryImpl()));

        clickLoginWithEmail();
        clickLoginWithGoogle();
    }

    private void clickLoginWithEmail() {
        loginWithEmail.setOnClickListener(v -> {
            String mail = email.getText().toString();
            String pass = password.getText().toString();

            loginViewModel.login(mail, pass, new LoginViewModel.OnLoginListener() {
                @Override
                public void onSuccess(String token) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                Log.d("LoginActivity", "Google sign-in successful. Email: " + account.getEmail());
                firebaseAuth(account); // Xác thực Firebase
            } catch (ApiException e) {
                Log.e("LoginActivity", "Google sign-in failed. Code: " + e.getStatusCode(), e);
                handleGoogleSignInError(e); // Hàm xử lý chi tiết lỗi
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


    /**
     * Xác thực người dùng với Firebase bằng token ID từ Google.
     * <p>
     * Phương thức này sử dụng token ID của Google để tạo đối tượng {@link com.google.firebase.auth.AuthCredential},
     * sau đó xác thực thông qua Firebase Authentication bằng cách gọi
     * {@code firebaseAuth.signInWithCredential(AuthCredential)}.
     *
     * @param account tài khoản người dùng nhận được từ Google sau khi người dùng đăng nhập.
     *                <p>
     *                Điều kiện tiên quyết:
     *                - Đối tượng FirebaseAuth (firebaseAuth) phải được khởi tạo trước khi gọi phương thức này.
     *                - Tham số {@code idToken} không được null.
     *                <p>
     *                Quá trình:
     *                1. Tạo đối tượng {@link com.google.firebase.auth.AuthCredential} bằng token ID.
     *                2. Gọi phương thức xác thực của Firebase.
     *                3. Nếu xác thực thành công:
     *                - Lấy thông tin người dùng từ Firebase thông qua {@link com.google.firebase.auth.FirebaseUser}.
     *                - Nếu thông tin người dùng không tồn tại, hiển thị thông báo lỗi.
     *                - Nếu tồn tại, lưu thông tin người dùng (ID, tên, avatar) vào cơ sở dữ liệu Firebase Realtime Database.
     *                - Chuyển sang màn hình chính ({@code Home}).
     *                4. Nếu xác thực thất bại, hiển thị thông báo lỗi.
     *                <p>
     *                Ví dụ:
     *                <pre>
     *                               String idToken = "your_google_id_token";
     *                               firebaseAuth(idToken);
     *                               </pre>
     *                <p>
     *                Xử lý lỗi:
     *                - Hiển thị thông báo lỗi nếu người dùng không được xác thực hoặc thông tin không tồn tại.
     */
    private void firebaseAuth(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("LoginActivity", "Firebase authentication successful. Email: "
                                    + (user != null ? user.getEmail() : "Unknown"));
                            startActivity(new Intent(LoginActivity.this, LogoutActivity.class));
                            finish();
                        } else {
                            Log.e("LoginActivity", "Firebase authentication failed.", task.getException());
                            String errorMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : getString(R.string.firebase_auth_failed);
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}