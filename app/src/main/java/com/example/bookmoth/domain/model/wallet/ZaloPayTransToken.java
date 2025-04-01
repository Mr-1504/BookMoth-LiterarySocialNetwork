package com.example.bookmoth.domain.model.wallet;

/**
 * Class đại diện cho token giao dịch của ZaloPay.
 */
public class ZaloPayTransToken {

    /**
     * Token giao dịch ZaloPay.
     */
    private String zptranstoken;

    /**
     * Khởi tạo một đối tượng {@code ZaloPayTransToken}.
     *
     * @param zptranstoken Chuỗi token giao dịch của ZaloPay.
     */
    public ZaloPayTransToken(String zptranstoken) {
        this.zptranstoken = zptranstoken;
    }

    /**
     * Lấy token giao dịch của ZaloPay.
     *
     * @return Chuỗi token giao dịch.
     */
    public String getZptranstoken() {
        return zptranstoken;
    }

    /**
     * Cập nhật token giao dịch của ZaloPay.
     *
     * @param zptranstoken Token giao dịch mới.
     */
    public void setZptranstoken(String zptranstoken) {
        this.zptranstoken = zptranstoken;
    }
}
