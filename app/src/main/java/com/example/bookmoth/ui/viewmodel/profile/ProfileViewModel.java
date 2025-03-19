package com.example.bookmoth.ui.viewmodel.profile;

import android.content.Context;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lớp ViewModel xử lý logic lấy dữ liệu hồ sơ từ server.
 */
public class ProfileViewModel {
    private final ProfileUseCase profileUseCase;

    /**
     * Khởi tạo ProfileViewModel với ProfileUseCase.
     *
     * @param profileUseCase Lớp chứa logic lấy dữ liệu hồ sơ.
     */
    public ProfileViewModel(ProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    /**
     * Lấy thông tin hồ sơ từ server và gọi callback tương ứng.
     *
     * @param context  Context của ứng dụng, dùng để lấy string resource.
     * @param listener Lắng nghe kết quả của quá trình lấy hồ sơ.
     */
    public void getProfile(Context context, final OnProfileListener listener) {
        profileUseCase.getProfile().enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        listener.onProfileSuccess(response.body());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else if (response.code() == 401) {
                    listener.onProfileFailure(context.getString(R.string.invalid_email));
                } else if (response.code() == 404) {
                    listener.onProfileFailure(context.getString(R.string.account_does_not_exist));
                } else {
                    listener.onProfileFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                listener.onErrorConnectToServer(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Interface dùng để lắng nghe kết quả khi lấy hồ sơ từ server.
     */
    public interface OnProfileListener {
        /**
         * Gọi khi lấy hồ sơ thành công.
         *
         * @param profile Đối tượng chứa thông tin hồ sơ.
         * @throws InterruptedException Nếu có lỗi xảy ra trong quá trình xử lý.
         */
        void onProfileSuccess(Profile profile) throws InterruptedException;

        /**
         * Gọi khi lấy hồ sơ thất bại do lỗi cụ thể từ server.
         *
         * @param error Chuỗi lỗi thông báo cho người dùng.
         */
        void onProfileFailure(String error);

        /**
         * Gọi khi xảy ra lỗi kết nối tới server.
         *
         * @param error Chuỗi lỗi thông báo cho người dùng.
         */
        void onErrorConnectToServer(String error);
    }
}
