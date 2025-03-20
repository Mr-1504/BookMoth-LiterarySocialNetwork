package com.example.bookmoth.domain.model.login;

/**
 * Class đại diện cho thông tin tài khoản của người dùng.
 */
public class Account {
    private int accountId;
    private String email;

    /**
     * Khởi tạo một đối tượng {@code Account}.
     *
     * @param accountId ID của tài khoản.
     * @param email     Địa chỉ email của tài khoản.
     */
    public Account(int accountId, String email) {
        this.accountId = accountId;
        this.email = email;
    }

    /**
     * Lấy ID của tài khoản.
     *
     * @return ID của tài khoản.
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Cập nhật ID của tài khoản.
     *
     * @param accountId ID mới của tài khoản.
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    /**
     * Lấy địa chỉ email của tài khoản.
     *
     * @return Email của tài khoản.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Cập nhật địa chỉ email của tài khoản.
     *
     * @param email Email mới của tài khoản.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
