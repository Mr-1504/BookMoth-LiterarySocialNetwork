package com.example.bookmoth.domain.model.post;

import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("work_id")
    private int work_id;
    @SerializedName("title")
    private String title;
    @SerializedName("cover_url")
    private String cover_url;

    public int getWorks_id() {
        return work_id;
    }
    public String getTitle() {
        return title;
    }

    public String getCover_url() {
        return cover_url;
    }

}
