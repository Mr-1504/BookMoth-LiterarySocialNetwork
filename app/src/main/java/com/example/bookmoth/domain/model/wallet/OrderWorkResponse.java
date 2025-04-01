package com.example.bookmoth.domain.model.wallet;

import java.math.BigDecimal;

public class OrderWorkResponse {
    private String transId;
    private String desc;
    private BigDecimal amount;

    public String getDesc() {
        return desc;
    }

    public String getTransId() {
        return transId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
