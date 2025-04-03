package com.example.bookmoth.data.remote.post;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Api {

        @SerializedName("data") // Đảm bảo đúng tên key trong JSON
        private String data;

        @SerializedName("profile_ids")
        private List<Integer> profile_ids;

        @SerializedName("checkBlood")
        private int checkBlood;

        @SerializedName("error")
        private String error;

        @SerializedName("message")
        private String message;

        public int getCheckBlood() {
            return checkBlood;
        }
        public String getError() {
            return error;
        }
        public String getMessage() {
            return message;
        }
        public String getData() {
            return data;
        }

        public List<Integer> getProfile_ids() {
            return profile_ids;
        }


}
