package com.example.bookmoth.domain.model.wallet;

import java.math.BigDecimal;

/**
 * Response trả về số dư của ví
 */
public class BalanceResponse {
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }
}
