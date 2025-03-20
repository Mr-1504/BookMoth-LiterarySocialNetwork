package com.example.bookmoth.data.model.login;

/**
 * Lớp đại diện cho yêu cầu làm mới token.
 */
public class RefreshTokenRequest {
    private String refreshToken;

    /**
     * Khởi tạo yêu cầu với refreshToken.
     *
     * @param refreshToken Token cần làm mới.
     */
    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
