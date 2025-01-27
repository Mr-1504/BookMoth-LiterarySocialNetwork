package com.example.bookmoth.utils.Helper;

import android.annotation.SuppressLint;

import com.example.bookmoth.utils.Helper.HMac.HMacUtil;

import org.jetbrains.annotations.NotNull;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Helpers {


    /**
     * tạo mã giao dịch với cấu trúc ngày (định dạng yyMMdd_hhmmss) + mã đơn hàng
     * @return String mã giao dịch
     */
    @NotNull
    @SuppressLint("DefaultLocale")
     public static String getAppTransId(String invoiceId) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatDateTime = new SimpleDateFormat("yyMMdd_hhmmss");
        String timeString = formatDateTime.format(new Date());
        return String.format("%s%s", timeString, invoiceId);
    }

    @NotNull
    public static String getMac(@NotNull String key, @NotNull String data) throws NoSuchAlgorithmException, InvalidKeyException {
        return Objects.requireNonNull(HMacUtil.HMacHexStringEncode(HMacUtil.HMACSHA256, key, data));
     }
}
