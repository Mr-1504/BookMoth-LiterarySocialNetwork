package com.example.bookmoth.utils.Api;

import com.example.bookmoth.utils.Constant.AppInfo;
import com.example.bookmoth.utils.Helper.Helpers;

import org.json.JSONObject;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateOrder {
    private class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;

        public CreateOrderData(String amount, String appUser, String invoiceId) throws Exception {
            long appTime = new Date().getTime();
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = appUser;
            AppTime = String.valueOf(appTime);
            Amount = amount;
            AppTransId = Helpers.getAppTransId(invoiceId);
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
            Description = "Merchant pay for order #" + AppTransId;
            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId,
                    this.AppTransId,
                    this.AppUser,
                    this.Amount,
                    this.AppTime,
                    this.EmbedData,
                    this.Items);

            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);

            System.out.println(AppId + " " + Mac + " " + AppTime);
        }
    }

    public JSONObject createOrder(String amount, String appUser, String invoiceId) throws Exception {
        CreateOrderData input = new CreateOrderData(amount, appUser, invoiceId);

        RequestBody formBody = new FormBody.Builder()
                .add("appid", input.AppId)
                .add("appuser", input.AppUser)
                .add("apptime", input.AppTime)
                .add("amount", input.Amount)
                .add("apptransid", input.AppTransId)
                .add("embeddata", input.EmbedData)
                .add("item", input.Items)
                .add("bankcode", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .build();

        return HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
    }
}

