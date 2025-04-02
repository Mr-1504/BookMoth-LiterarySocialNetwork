package com.example.bookmoth.ui.viewmodel.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.profile.FollowResponse;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.model.profile.ProfileResponse;
import com.example.bookmoth.domain.model.profile.UsernameResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.login.LoginActivity;

import org.json.JSONObject;

import java.util.List;
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


    /**
     * Chỉnh sửa thông tin hồ sơ người dùng và gọi callback tương ứng.
     *
     * @param context  Context của ứng dụng, dùng để lấy string resource.
     * @param params   thông tin hồ sơ cần chỉnh sửa.
     * @param avatar   ảnh đại diện mới.
     * @param cover    ảnh bìa mới.
     * @param listener Lắng nghe kết quả của quá trình chỉnh sửa.
     */
    public void editProfile(
            Context context, Map<String, RequestBody> params,
            MultipartBody.Part avatar, MultipartBody.Part cover,
            final OnEditProfile listener
    ) {
        profileUseCase.editProfile(params, avatar, cover).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
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
            public void onFailure(Call<Profile> call, Throwable t) {
                listener.onError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Tìm kiếm hồ sơ người dùng theo tên và gọi callback tương ứng.
     *
     * @param context      Context của ứng dụng, dùng để lấy string resource.
     * @param searchString tên cần tìm kiếm.
     * @param listener     Lắng nghe kết quả của quá trình tìm kiếm.
     */
    public void searchProfile(Context context, String searchString, final OnSearchProfile listener) {
        profileUseCase.searchProfile(searchString).enqueue(new Callback<List<ProfileResponse>>() {
            @Override
            public void onResponse(Call<List<ProfileResponse>> call, Response<List<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else if (response.code() == 400) {
                    listener.OnError(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.OnError(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.OnError(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 404) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_PROFILE".equals(errorCode)) {
                                listener.OnError(context.getString(R.string.cannot_process_request));
                            } else {
                                listener.OnError(context.getString(R.string.not_found_profile));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.OnError(context.getString(R.string.undefined_error));
                        }
                    }
                } else {
                    listener.OnError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<List<ProfileResponse>> call, Throwable t) {
                listener.OnError(context.getString(R.string.error_connecting_to_server));
            }
        });
    }

    /**
     * Follow người dùng.
     *
     * @param context   Context của ứng dụng, dùng để lấy string resource.
     * @param profileId id của người dùng cần follow
     * @param listener  Lắng nghe kết quả của quá trình follow.
     */
    public void follow(Context context, String profileId, final OnFollowProfile listener) {
        profileUseCase.follow(profileId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.onError(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 404) {
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

    public void unfollow(Context context, String profileId, final OnFollowProfile listener) {
        profileUseCase.unfollow(profileId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess();
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.onError(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 404) {
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
     * Kiểm tra xem người dùng đã follow profileId chưa.
     *
     * @param context   Context của ứng dụng, dùng để lấy string resource.
     * @param profileId id của người dùng cần kiểm tra.
     * @param listener  Lắng nghe kết quả của quá trình kiểm tra.
     */
    public void isFollow(Context context, String profileId, final OnIsFollowProfile listener) {
        profileUseCase.isFollowing(profileId).enqueue(new Callback<FollowResponse>() {
            @Override
            public void onResponse(Call<FollowResponse> call, Response<FollowResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    listener.onSuccess(response.body().isFollowing());
                } else if (response.code() == 400) {
                    listener.onError(context.getString(R.string.invalid_data));
                } else if (response.code() == 401) {
                    if (response.errorBody() != null) {
                        try {
                            JSONObject json = new JSONObject(response.errorBody().string());
                            String errorCode = json.optString("error_code", "");

                            if ("INVALID_TOKEN".equals(errorCode)) {
                                Intent intent = new Intent(context, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                context.startActivity(intent);
                            } else {
                                listener.onError(context.getString(R.string.undefined_error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            listener.onError(context.getString(R.string.undefined_error));
                        }
                    }
                } else if (response.code() == 404) {
                    listener.onError(context.getString(R.string.cannot_process_request));
                } else {
                    listener.onError(context.getString(R.string.undefined_error));
                }
            }

            @Override
            public void onFailure(Call<FollowResponse> call, Throwable t) {
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

    /**
     * Interface dùng để lắng nghe kết quả khi chỉnh sửa hồ sơ.
     */
    public interface OnEditProfile {
        void onSuccess(Profile profile);

        void onError(String error);
    }

    /**
     * Interface dùng để lắng nghe kết quả khi tìm kiếm hồ sơ.
     */
    public interface OnSearchProfile {
        void onSuccess(List<ProfileResponse> responses);

        void OnError(String error);
    }

    /**
     * Interface dùng để lắng nghe kết quả khi follow/unfollow profile.
     */
    public interface OnFollowProfile {
        void onSuccess();

        void onError(String error);
    }

    public interface OnIsFollowProfile {
        void onSuccess(boolean isFollow);

        void onError(String error);
    }

    /**
     * Dừng việc sử dụng ViewModel.
     */
    public void clear() {
        executor.shutdown();
    }

}
