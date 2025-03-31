package com.example.bookmoth.ui.activity.option;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.repository.login.LoginRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.data.repository.wallet.WalletRepositoryImpl;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.usecase.login.LoginUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.activity.login.LoginActivity;
import com.example.bookmoth.ui.activity.profile.ProfileActivity;
import com.example.bookmoth.ui.activity.wallet.ConfirmActivity;
import com.example.bookmoth.ui.activity.wallet.CreatePinActivity;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.login.LoginViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.example.bookmoth.ui.activity.wallet.WalletActivity;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 *
 */
public class OptionActivity extends AppCompatActivity {

    private Button btnProfile, btnWallet, btnLogout, btnBuy;
    private WalletViewModel walletViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBuy = findViewById(R.id.btnbuy);
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnViewProfile);
        btnWallet = findViewById(R.id.btnWallet);
        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));

        clickLogout();
        clickWallet();
        clickProfile();
        clickBuy();
    }

    private void clickBuy() {
        btnBuy.setOnClickListener(view -> {
            Intent intent = new Intent(OptionActivity.this, ConfirmActivity.class);
            intent.putExtra("type", 4);
            intent.putExtra("workId", 1);
            startActivity(intent);
        });
    }


    private void clickProfile() {
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void clickWallet() {
        btnWallet.setOnClickListener(v -> {
            LoadingUtils.showLoading(getSupportFragmentManager());
            walletViewModel.checkWalletExist(this, new WalletViewModel.OnWalletListener() {
                @Override
                public void onSuccess(BalanceResponse balanceResponse) {
                    LoadingUtils.hideLoading();
                    Intent intent = new Intent(OptionActivity.this, WalletActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailed(String error) {
                    LoadingUtils.hideLoading();
                    if(error.equals(getString(R.string.wallet_does_not_exist))){
                        Intent intent = new Intent(OptionActivity.this, CreatePinActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(OptionActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    private void clickLogout() {
        btnLogout.setOnClickListener(v -> {
            if (InternetHelper.isNetworkAvailable(this)){
                LoadingUtils.showLoading(getSupportFragmentManager());
                logout();
                return;
            }
            Toast.makeText(OptionActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);
        client.signOut();

        new LoginViewModel(new LoginUseCase(new LoginRepositoryImpl())).logout(this);

        new ProfileViewModel(new ProfileUseCase(
                new LocalProfileRepositoryImpl(
                        this, ProfileDatabase.getInstance(this).profileDao()),
                new ProfileRepositoryImpl()
        )).deleteProfile();
        ImageCache.deleteBitmap(this, "avatar.png");
        ImageCache.deleteBitmap(this, "cover.png");

        SecureStorage.clearToken();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        LoadingUtils.hideLoading();
        startActivity(intent);
    }
}