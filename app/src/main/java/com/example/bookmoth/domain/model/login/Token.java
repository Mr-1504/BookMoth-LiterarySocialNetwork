package com.example.bookmoth.domain.model.login;

import com.google.gson.annotations.SerializedName;

/**
 * Lớp đại diện cho thông tin token của người dùng.
 */
public class Token {

    /**
     * Token JWT dùng để xác thực các request API.
     */
    @SerializedName("jwtToken")
    private String jwtToken;

    /**
     * Token dùng để làm mới JWT khi hết hạn.
     */
    @SerializedName("refreshToken")
    private String refreshToken;

    /**
     * Khởi tạo một đối tượng {@code Token}.
     *
     * @param jwtToken     Chuỗi JWT token.
     * @param refreshToken Chuỗi refresh token.
     */
    public Token(String jwtToken, String refreshToken) {
        this.jwtToken = jwtToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Lấy JWT token hiện tại.
     *
     * @return Chuỗi JWT token.
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Cập nhật JWT token.
     *
     * @param jwtToken JWT token mới.
     */
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    /**
     * Lấy refresh token hiện tại.
     *
     * @return Chuỗi refresh token.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Cập nhật refresh token.
     *
     * @param refreshToken Refresh token mới.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
