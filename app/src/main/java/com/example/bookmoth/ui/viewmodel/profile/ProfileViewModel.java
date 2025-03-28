package com.example.bookmoth.ui.viewmodel.profile;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.UsernameResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Lớp ViewModel xử lý logic lấy dữ liệu hồ sơ từ server.
 */
public class ProfileViewModel {
    private final ProfileUseCase profileUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


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
                    listener.onProfileSuccess(response.body());
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
                listener.onProfileFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Lấy thông tin hồ sơ từ server theo id và gọi callback tương ứng.
     *
     * @param context   Context của ứng dụng, dùng để lấy string resource.
     * @param profileId id của hồ sơ người dùng.
     * @param listener  Lắng nghe kết quả của quá trình lấy hồ sơ.
     */
    public void getProfileById(Context context, String profileId, final OnProfileListener listener) {
        profileUseCase.getProfileById(profileId).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onProfileSuccess(response.body());
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


    /**
     * Kiểm tra xem username đã tồn tại hay chưa.
     *
     * @param context  Context của ứng dụng, dùng để lấy string resource.
     * @param username username cần kiểm tra.
     * @param listener Lắng nghe kết quả của quá trình kiểm tra.
     */
    public void checkUsername(Context context, String username, final OnCheckUsernameListener listener) {
        profileUseCase.checkUsername(username).enqueue(new Callback<UsernameResponse>() {
            @Override
            public void onResponse(Call<UsernameResponse> call, Response<UsernameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onProfileSuccess(response.body().isExists());
                } else {
                    listener.onProfileFailure(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<UsernameResponse> call, Throwable t) {
                listener.onProfileFailure(context.getString(R.string.error_connecting_to_server));
            }
        });
    }


    public void editProfile(
            Context context, Map<String, RequestBody> params,
            MultipartBody.Part avatar, MultipartBody.Part cover,
            final OnEditProfile listener
    ) {
        profileUseCase.editProfile(params, avatar, cover).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.try_again));
                } else if (response.code() == 401) {
                    listener.onError(context.getString(R.string.error_auth));
                } else if (response.code() == 404) {
                    listener.onError(context.getString(R.string.error_account));
                } else if (response.code() == 422) {
                    listener.onError(context.getString(R.string.cannot_process_request));
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }


    /**
     * Xóa thông tin hồ sơ người dùng khỏi bộ nhớ cục bộ.
     */
    public void deleteProfile() {
        executor.execute(profileUseCase::deleteProfileLocal);
    }

    /**
     * Lưu thông tin hồ sơ người dùng vào bộ nhớ cục bộ.
     *
     * @param profile Đối tượng chứa thông tin hồ sơ người dùng.
     */
    public void saveProfile(Profile profile) {
        executor.execute(() -> profileUseCase.saveProfile(profile));
    }


    /**
     * Lấy thông tin hồ sơ người dùng từ bộ nhớ cục bộ.
     *
     * @param listener Lắng nghe kết quả khi lấy hồ sơ.
     */
    public void getProfileLocal(OnProfileListener listener) {
        executor.execute(() -> {
            Profile profile = profileUseCase.getProfileLocal();
            new Handler(Looper.getMainLooper()).post(() -> listener.onProfileSuccess(profile));
        });
    }


    /**
     * Kiểm tra xem hồ sơ người dùng đã được lưu trong bộ nhớ cục bộ hay chưa.
     *
     * @return True nếu đã lưu, ngược lại trả về false.
     */
    public void isProfileExist(Consumer<Boolean> callback) {
        executor.execute(() -> {
            boolean exists = profileUseCase.isProfileExist();
            new Handler(Looper.getMainLooper()).post(() -> callback.accept(exists));
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
         */
        void onProfileSuccess(Profile profile);

        /**
         * Gọi khi lấy hồ sơ thất bại do lỗi cụ thể từ server.
         *
         * @param error Chuỗi lỗi thông báo cho người dùng.
         */
        void onProfileFailure(String error);
    }

    /**
     * Interface dùng để lắng nghe kết quả khi kiểm tra username.
     */
    public interface OnCheckUsernameListener {
        /**
         * Gọi khi kiểm tra username thành công.
         */
        void onProfileSuccess(boolean exists);

        /**
         * Gọi khi kiểm tra username thất bại do lỗi cụ thể từ server.
         *
         * @param error Chuỗi lỗi thông báo cho người dùng.
         */
        void onProfileFailure(String error);
    }

    public interface OnEditProfile {
        void onSuccess();

        void onError(String error);
    }

    public void clear() {
        executor.shutdown();
    }

}
