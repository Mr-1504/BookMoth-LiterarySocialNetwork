package com.example.bookmoth.domain.repository.wallet;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.model.wallet.OrderWorkResponse;

import retrofit2.Call;
import retrofit2.http.Body;

/**
 * Interface chứa các phương thức liên quan đến ví
 */
public interface WalletRepository {
    /**
     * Lấy số dư của ví
     * @return số dư của ví
     */
    Call<BalanceResponse> getBalance();

    /**
     * Tạo ví mới
     * @return kết quả tạo ví
     */
    Call<Void> createWallet(String pin);

    /**
     * Xác nhận mã pin
     * @return kết quả xác nhận
     */
    Call<Void> confirmPin(String pin);

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    Call<Void> checkWalletExist();

    /**
     * Tạo đơn hàng để nạp tiền qua ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán (đơn vị: VND).
     * @param description     Mô tả giao dịch.
     * @param transactionType Loại giao dịch (1: nạp, 2: rút, 3: chuyển tiền, 4: thanh toán).
     * @return Đối tượng Call chứa phản hồi từ ZaloPay, bao gồm mã token giao dịch.
     */
    Call<ZaloPayTokenResponse> createOrder(long amount, String description, int transactionType);

    /**
     * Tạo đơn hàng để mua sản phẩm.
     *
     * @param workId    ID của sản phẩm.
     * @param orderTime Thời gian đặt hàng.
     * @param mac       Địa chỉ MAC của thiết bị.
     * @return Đối tượng Call chứa phản hồi từ server.
     */
    Call<OrderWorkResponse> orderProduct(int workId, String orderTime, String mac);

    /**
     * Cập nhật phương thức thanh toán cho giao dịch.
     *
     * @param transactionId   ID của giao dịch.
     * @param paymentMethodId ID của phương thức thanh toán.
     * @return Đối tượng Call chứa phản hồi từ server.
     */
    Call<Void> updatePaymentMethod(String transactionId, int paymentMethodId);

    /**
     * Thanh toán giao dịch.
     *
     * @param transactionId ID của giao dịch.
     * @return Đối tượng Call chứa phản hồi từ server.
     */
    Call<Void> payment(String transactionId);

    /**
     * Tạo đơn hàng để thanh toán qua ZaloPay.
     *
     * @param transactionId ID của giao dịch.
     * @return Đối tượng Call chứa phản hồi từ ZaloPay, bao gồm mã token giao dịch.
     */
    Call<ZaloPayTokenResponse> createZaloPayOrder(@Body String transactionId);
}
