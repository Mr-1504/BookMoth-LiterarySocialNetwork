package com.example.bookmoth.data.remote.notification;

import com.example.bookmoth.data.model.notification.FcmTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface định nghĩa API liên quan đến thông báo (Notification).
 */
public interface NotificationApiService {

    /**
     * Gửi token FCM để đăng ký thiết bị nhận thông báo.
     *
     * @param fcmTokenRequest Đối tượng {@link FcmTokenRequest} chứa token FCM và ID thiết bị.
     * @return {@link Call} với kiểu trả về là {@code Void}, không có dữ liệu trả về.
     */
    @POST("/api/notification/register")
    Call<Void> registerDevice(@Body FcmTokenRequest fcmTokenRequest);
}
