package com.example.bookmoth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.Utils.InternetHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Login extends AppCompatActivity {

    private Button loginWithGoogle;
    private FirebaseDatabase database;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient client;
    private FirebaseAuth firebaseAuth;

    int RC_LOGIN = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setUp();
        clickLogin();
    }


    /**
     * Phương thức `clickLogin` dùng để thiết lập sự kiện khi người dùng nhấn nút đăng nhập với Google.<br>
     * - Trước khi thực hiện đăng nhập, phương thức sẽ kiểm tra kết nối Internet của thiết bị. <br>
     * - Nếu có kết nối Internet, phương thức sẽ gọi phương thức `loginWithGoogle()` để tiến hành đăng nhập. <br>
     * - Nếu không có kết nối Internet, phương thức sẽ hiển thị một thông báo cho người dùng biết rằng không có kết nối mạng.
     */
    private void clickLogin() {
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra kết nối Internet trước khi thực hiện đăng nhập
                if (InternetHelper.isNetworkAvailable(Login.this)) {
                    loginWithGoogle();
                } else {
                    // Nếu không có kết nối, hiển thị thông báo cho người dùng
                    Toast.makeText(Login.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
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
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                Log.e("Api GoogleSignIn", "Error: " + e.getMessage() + "\n" + R.string.WEB_CLIENT_ID, e);
                Toast.makeText(Login.this, R.string.LoginFailedMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Xác thực người dùng với Firebase bằng token ID từ Google.
     *
     * Phương thức này sử dụng token ID của Google để tạo đối tượng {@link com.google.firebase.auth.AuthCredential},
     * sau đó xác thực thông qua Firebase Authentication bằng cách gọi
     * {@code firebaseAuth.signInWithCredential(AuthCredential)}.
     *
     * @param idToken Token ID nhận được từ Google sau khi người dùng đăng nhập.
     *
     * Điều kiện tiên quyết:
     * - Đối tượng FirebaseAuth (firebaseAuth) phải được khởi tạo trước khi gọi phương thức này.
     * - Tham số {@code idToken} không được null.
     *
     * Quá trình:
     * 1. Tạo đối tượng {@link com.google.firebase.auth.AuthCredential} bằng token ID.
     * 2. Gọi phương thức xác thực của Firebase.
     * 3. Nếu xác thực thành công:
     *    - Lấy thông tin người dùng từ Firebase thông qua {@link com.google.firebase.auth.FirebaseUser}.
     *    - Nếu thông tin người dùng không tồn tại, hiển thị thông báo lỗi.
     *    - Nếu tồn tại, lưu thông tin người dùng (ID, tên, avatar) vào cơ sở dữ liệu Firebase Realtime Database.
     *    - Chuyển sang màn hình chính ({@code Home}).
     * 4. Nếu xác thực thất bại, hiển thị thông báo lỗi.
     *
     * Ví dụ:
     * <pre>
     * String idToken = "your_google_id_token";
     * firebaseAuth(idToken);
     * </pre>
     *
     * Xử lý lỗi:
     * - Hiển thị thông báo lỗi nếu người dùng không được xác thực hoặc thông tin không tồn tại.
     */
    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(Login.this, R.string.LoginFailedMessage, Toast.LENGTH_SHORT).show();
                                Log.w("User Information", "Error: User Information");
                                return;
                            }
                            HashMap<String, String> map = new HashMap<>();
                            map.put("id", user.getUid());
                            map.put("name", user.getDisplayName());
                            map.put("avatar", Objects.requireNonNull(user.getPhotoUrl()).toString());
                            database.getReference().child("userss")
                                    .child(user.getUid())
                                    .setValue(map);

                            Intent intent = new Intent(Login.this, Home.class);
                            startActivity(intent);
                        } else {
                            Log.e("GoogleSignIn", "Error: Login Failed!");
                            Toast.makeText(Login.this, R.string.LoginFailedMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    /**
     * Initialize basic components like Button, Firebase, Google Sign In.
     * <br>
     * Khởi tạo các thành phần cơ bản như ánh xạ button, khởi tạo Firebase, Google Sign In.
     */
    private void setUp() {
        loginWithGoogle = findViewById(R.id.button);
        database = FirebaseDatabase.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(Login.this, signInOptions);
        firebaseAuth = FirebaseAuth.getInstance();
    }


}