package com.example.bookmoth.domain.usecase.wallet;

import com.example.bookmoth.data.model.payment.ZaloPayTokenResponse;
import com.example.bookmoth.domain.model.wallet.BalanceResponse;
import com.example.bookmoth.domain.model.wallet.OrderWorkResponse;
import com.example.bookmoth.domain.repository.wallet.WalletRepository;

import retrofit2.Call;

/**
 * Lớp chứa các phương thức liên quan đến ví
 */
public class WalletUseCase {
    private final WalletRepository walletRepository;

    /**
     * Khởi tạo WalletUseCase
     * @param walletRepository repository chứa các phương thức gọi API liên quan đến ví
     */
    public WalletUseCase(WalletRepository walletRepository){
        this.walletRepository = walletRepository;
    }

    /**
     * Lấy số dư của ví
     * @return số dư của ví
     */
    public Call<BalanceResponse> getBalance(){
        return walletRepository.getBalance();
    }

    /**
     * Tạo ví mới
     * @return kết quả tạo ví
     */
    public Call<Void> createWallet(String pin) {
        return walletRepository.createWallet(pin);
    }

    /**
     * Xác nhận mã pin
     * @return kết quả xác nhận
     */
    public Call<Void> confirmPin(String pin) {
        return walletRepository.confirmPin(pin);
    }

    /**
     * Kiểm tra ví đã tồn tại chưa
     * @return kết quả kiểm tra
     */
    public Call<Void> checkWalletExist() {
        return walletRepository.checkWalletExist();
    }

    /**
     * Tạo đơn hàng thanh toán qua ZaloPay.
     *
     * @param amount          Số tiền cần thanh toán.
     * @param description     Mô tả giao dịch.
     * @param transactionType Loại giao dịch (true: giao dịch ngay, false: giao dịch tạm giữ).
     * @return Đối tượng Call chứa phản hồi từ ZaloPay.
     */
    public Call<ZaloPayTokenResponse> createOrder(long amount, String description, int transactionType) {
        return walletRepository.createOrder(amount, description, transactionType);
    }

    /**
     * Tạo giao dịch mua sản phẩm.
     *
     * @param workId    ID công việc cần mua.
     * @param orderTime Thời gian tạo đơn hàng.
     * @param mac       Mã xác thực.
     * @return {@link Call} chứa {@link String}, mã giao dịch.
     */
    public Call<OrderWorkResponse> orderProduct(int workId, String orderTime, String mac) {
        return walletRepository.orderProduct(workId, orderTime, mac);
    }


    /**
     * Cập nhật phương thức thanh toán cho giao dịch.
     *
     * @param transactionId   ID của giao dịch.
     * @param paymentMethodId ID của phương thức thanh toán.
     * @return Đối tượng Call chứa phản hồi từ server.
     */
    public Call<Void> updatePaymentMethod(String transactionId, int paymentMethodId) {
        return walletRepository.updatePaymentMethod(transactionId, paymentMethodId);
    }

    /**
     * Thanh toán giao dịch.
     *
     * @param transactionId ID của giao dịch.
     * @return Đối tượng Call chứa phản hồi từ server.
     */
    public Call<Void> payment(String transactionId) {
        return walletRepository.payment(transactionId);
    }

    /**
     * Tạo đơn hàng để thanh toán qua ZaloPay.
     *
     * @param transactionId ID của giao dịch.
     * @return {@link Call} chứa {@link ZaloPayTokenResponse}, phản hồi từ ZaloPay.
     */
    public Call<ZaloPayTokenResponse> createZaloPayOrder(String transactionId) {
        return walletRepository.createZaloPayOrder(transactionId);
    }
}
