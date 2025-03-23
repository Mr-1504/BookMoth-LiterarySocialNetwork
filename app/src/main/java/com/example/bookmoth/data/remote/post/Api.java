package com.example.bookmoth.data.remote.post;

import com.google.gson.annotations.SerializedName;

public class Api {

        @SerializedName("data") // Đảm bảo đúng tên key trong JSON
        private String data;

        public String getData() {
            return data;
        }


}
