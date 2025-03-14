package com.example.bookmoth.ui.viewmodel;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel {
    private final ProfileUseCase profileUseCase;

    public ProfileViewModel(ProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    public void getProfile(Context context, final OnProfileListener listener) {
        profileUseCase.getProfile().enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onProfileSuccess(response.body());
                } else if(response.code() == 401) {
                    listener.onProfileFailure(context.getString(R.string.invalid_email));
                } else if (response.code() == 404) {
                    listener.onProfileFailure(context.getString(R.string.account_does_not_exist));
                } else {
                    listener.onProfileFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                listener.onProfileFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    public interface OnProfileListener {
        void onProfileSuccess(Profile profile);
        void onProfileFailure(String error);
    }
}
