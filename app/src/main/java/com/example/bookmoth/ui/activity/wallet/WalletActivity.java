package com.example.bookmoth.ui.activity.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookmoth.core.utils.Extension;
import com.example.bookmoth.databinding.ActivityWalletBinding;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.data.repository.wallet.WalletRepositoryImpl;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.domain.usecase.wallet.WalletUseCase;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;

import java.text.Normalizer;

public class WalletActivity extends AppCompatActivity {

    private ImageView btnDeposit;
    private WalletViewModel walletViewModel;
    private ProfileViewModel profileViewModel;
    private TextView userName, userBalance, txtBack, accountName;
    private ActivityWalletBinding binding;
    private Profile me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        getMe();
        clickDeposit();
        clickReturn();
    }

    private void clickReturn() {
        txtBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void init() {
        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));

        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao());

        profileViewModel = new ProfileViewModel(new ProfileUseCase(localRepo, new ProfileRepositoryImpl()));

        userName = binding.username;
        userBalance = binding.balance;
        btnDeposit = binding.deposit;
        txtBack = binding.txtBack;
        accountName = binding.accountName;
    }

    private void getMe() {
        profileViewModel.isProfileExist(exist -> {
            if (exist) {
                profileViewModel.getProfileLocal(new ProfileViewModel.OnProfileListener() {
                    @Override
                    public void onProfileSuccess(Profile profile) {
                        if (profile == null) return;
                        getBalance(profile);
                        me = profile;
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        Toast.makeText(WalletActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
                    @Override
                    public void onProfileSuccess(Profile profile) {
                        if (profile == null) return;
                        runOnUiThread(() -> {
                            profileViewModel.saveProfile(profile);
                            getBalance(profile);
                            me = profile;
                        });
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        Toast.makeText(WalletActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void getBalance(Profile profile) {
        walletViewModel.getBalance(this, new WalletViewModel.OnWalletListener() {
            @Override
            public void onSuccess(BalanceResponse balanceResponse) {
                setWallet(profile, balanceResponse);
            }

            @Override
            public void onFailed(String error) {
                Toast.makeText(WalletActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setWallet(Profile profile, BalanceResponse balance) {
        userName.setText(profile.getUsername());
        userBalance.setText(String.format("%s VND", Extension.bigDecimalToAmount(balance.getBalance())));

        String normalName = Normalizer.normalize(
                profile.getLastName() + " " + profile.getFirstName(), Normalizer.Form.NFD);
        accountName.setText(normalName.replaceAll("\\p{M}", "").toUpperCase());
    }


    private void clickDeposit() {
        btnDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(this, DepositActivity.class);
            String balance = userBalance.getText().toString();
            intent.putExtra("balance", balance.substring(0, balance.length() - 4));
            intent.putExtra("me", me);
            startActivity(intent);
        });
    }


}