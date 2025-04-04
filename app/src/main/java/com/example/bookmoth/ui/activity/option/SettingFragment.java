package com.example.bookmoth.ui.activity.option;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.InternetHelper;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
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
import com.example.bookmoth.ui.activity.wallet.CreatePinActivity;
import com.example.bookmoth.ui.activity.wallet.WalletActivity;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.login.LoginViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.example.bookmoth.ui.viewmodel.wallet.WalletViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * A fragment representing a list of Items.
 */
public class SettingFragment extends Fragment {

    private ImageView avatar;
    private CardView btnProfile, btnWallet, btnLogout;
    private TextView name;
    private WalletViewModel walletViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_list, container, false);

        avatar = view.findViewById(R.id.imgAvatar);
        btnProfile = view.findViewById(R.id.btnViewProfile);
        btnWallet = view.findViewById(R.id.btnViewWallet);
        btnLogout = view.findViewById(R.id.btnLogout);
        name = view.findViewById(R.id.profileName);
        walletViewModel = new WalletViewModel(new WalletUseCase(new WalletRepositoryImpl()));

        clickLogout();
        clickWallet();
        clickProfile();
        return view;
    }

    private void clickProfile() {
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void clickWallet() {
        btnWallet.setOnClickListener(v -> {
            LoadingUtils.showLoading(getParentFragmentManager());
            walletViewModel.checkWalletExist(getContext(), new WalletViewModel.OnWalletListener() {
                @Override
                public void onSuccess(BalanceResponse balanceResponse) {
                    LoadingUtils.hideLoading();
                    Intent intent = new Intent(getContext(), WalletActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailed(String error) {
                    LoadingUtils.hideLoading();
                    if(error.equals(getString(R.string.wallet_does_not_exist))){
                        Intent intent = new Intent(getContext(), CreatePinActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    private void clickLogout() {
        btnLogout.setOnClickListener(v -> {
            if (InternetHelper.isNetworkAvailable(getContext())){
                LoadingUtils.showLoading(getParentFragmentManager());
                logout();
                return;
            }
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        if(!InternetHelper.isNetworkAvailable(getContext())){
            LoadingUtils.hideLoading();
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            return;
        }
        new LoginViewModel(new LoginUseCase(new LoginRepositoryImpl())).logout(getContext(), new LoginViewModel.OnLogoutListener() {
            @Override
            public void onSuccess() {
                try{
                    new ProfileViewModel(new ProfileUseCase(
                            new LocalProfileRepositoryImpl(
                                    getContext(), ProfileDatabase.getInstance(getContext()).profileDao()),
                            new ProfileRepositoryImpl()
                    )).deleteProfile();
                    ImageCache.deleteBitmap(getContext(), "avatar.png");
                    ImageCache.deleteBitmap(getContext(), "cover.png");
                    SecureStorage.clearToken();

                    GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.WEB_CLIENT_ID))
                            .requestEmail()
                            .build();

                    GoogleSignInClient client = GoogleSignIn.getClient(getContext(), signInOptions);
                    client.signOut();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    LoadingUtils.hideLoading();
                    startActivity(intent);
                }
                catch (Exception ex){
                    LoadingUtils.hideLoading();
                    ex.printStackTrace();
                }

            }

            @Override
            public void onError(String error) {
                LoadingUtils.hideLoading();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });


    }
}