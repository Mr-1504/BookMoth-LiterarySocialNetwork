package com.example.bookmoth.data.model.notification;

/**
 * Class đại diện cho yêu cầu gửi token FCM lên server.
 */
public class FcmTokenRequest {
    private String Token;
    private String deviceId;

    /**
     * Khởi tạo đối tượng yêu cầu gửi token FCM.
     *
     * @param token    Token FCM của thiết bị.
     * @param deviceId ID của thiết bị.
     */
    public FcmTokenRequest(String token, String deviceId) {
        this.Token = token;
        this.deviceId = deviceId;
    }
}
