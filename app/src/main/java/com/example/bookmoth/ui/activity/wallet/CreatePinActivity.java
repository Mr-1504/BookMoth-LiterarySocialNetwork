package com.example.bookmoth.ui.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookmoth.R;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.data.repository.wallet.WalletRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.dialogs.PasswordPopup;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;

/**
 * Lớp Activity hiển thị giao diện tạo mã PIN.
 */
public class CreatePinActivity extends AppCompatActivity {

    private ImageView avatar;
    private TextView tvWelcome, txtBack;
    private Button next;
    private WalletViewModel walletViewModel;
    private PasswordPopup passwordPopup;
    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_pin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        clickNext();
        clickBack();
    }

    /**
     * Xử lý sự kiện khi click vào nút quay lại.
     */
    private void clickBack() {
        txtBack.setOnClickListener(view -> {
            finish();
        });
    }

    /**
     * Xử lý sự kiện khi click vào nút tiếp theo.
     */
    private void clickNext() {
        next.setOnClickListener(v -> {
            passwordPopup = new PasswordPopup(password -> {
                passwordPopup.dismiss();
                confirmPin(password);
            }, "Nhập PIN để mở ví");

            passwordPopup.show(getSupportFragmentManager(), passwordPopup.getTag());
        });
    }

    private void confirmPin(String inputed_password) {
        passwordPopup = new PasswordPopup(password -> {
            if (inputed_password.equals(password)) {
                LoadingUtils.showLoading(getSupportFragmentManager());
                walletViewModel.createWallet(CreatePinActivity.this, password, new WalletViewModel.OnWalletListener() {
                    @Override
                    public void onSuccess(BalanceResponse balanceResponse) {
                        Toast.makeText(
                                CreatePinActivity.this,
                                getString(R.string.create_wallet_successfully),
                                Toast.LENGTH_SHORT
                        ).show();

                        passwordPopup.dismiss();
                        LoadingUtils.hideLoading();
                        Intent intent = new Intent(CreatePinActivity.this, WalletActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailed(String error) {
                        LoadingUtils.hideLoading();
                        passwordPopup.setErrorMessage(error);
                    }
                });
            } else {
                passwordPopup.setErrorMessage("Mã PIN không khớp");
            }
        }, "Nhập lại PIN");

        passwordPopup.show(getSupportFragmentManager(), "PasswordPopup");
    }


    /**
     * Khởi tạo giao diện.
     */
    private void init() {
        txtBack = findViewById(R.id.txtBack);
        avatar = findViewById(R.id.imgAvatar);
        tvWelcome = findViewById(R.id.tvWelcome);
        next = findViewById(R.id.btnNext);

        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));

        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
        );

        profileViewModel.isProfileExist(exist -> {
            if (exist) {
                getProfileLocal(profileViewModel);
            } else {
                getProfileFromServer(profileViewModel);
            }
        });
    }


    /**
     * Lấy thông tin hồ sơ từ local.
     *
     * @param profileViewModel ViewModel chứa logic lấy dữ liệu hồ sơ.
     */
    private void getProfileLocal(ProfileViewModel profileViewModel) {
        profileViewModel.getProfileLocal(new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                tvWelcome.setText(String.format("%s %s",
                        profile.getFirstName(), getString(R.string.welcom_to_bookmoth))
                );

                avatar.setImageBitmap(ImageCache.loadBitmap(CreatePinActivity.this, "avatar.png"));
            }

            @Override
            public void onProfileFailure(String error) {

            }
        });
    }

    /**
     * Lấy thông tin hồ sơ từ server.
     *
     * @param profileViewModel ViewModel chứa logic lấy dữ liệu hồ sơ.
     */
    private void getProfileFromServer(ProfileViewModel profileViewModel){
        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                profileViewModel.saveProfile(profile);

                tvWelcome.setText(String.format("%s %s",
                        profile.getFirstName(), getString(R.string.welcom_to_bookmoth))
                );

                Glide.with(CreatePinActivity.this)
                        .load(profile.getAvatar())
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.avatar)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(200, 200)
                        .thumbnail(0.1f)
                        .into(avatar);
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(CreatePinActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}