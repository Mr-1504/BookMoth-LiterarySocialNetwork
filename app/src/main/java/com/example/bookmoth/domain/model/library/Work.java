package com.example.bookmoth.domain.model.library;

import java.io.Serializable;

public class Work implements Serializable {
    private int work_id;
    private int profile_id;
    private String author_name;
    private String title;
    private String post_date;
    private Double price;
    private int view_count;
    private String description;
    private String cover_url;

    public Work() {}

    public int getWork_id() {
        return work_id;
    }

    public int getProfile_id() {
        return profile_id;
    }
    public String getAuthor_name() {
        return author_name;
    }

    public String getTitle() {
        return title;
    }

    public String getPost_date() {
        return post_date;
    }

    public Double getPrice() {
        return price;
    }

    public int getView_count() {
        return view_count;
    }

    public String getDescription() {
        return description;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }

    void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public static void cloneValue(Work to, Work from) {
        to.setWork_id(from.getWork_id());
        to.setDescription(from.description);
        to.setPrice(from.getPrice());
        to.setAuthor_name(from.getAuthor_name());
        to.setTitle(from.getTitle());
        to.setProfile_id(from.getProfile_id());
        to.setPost_date(from.getPost_date());
        to.setCover_url(from.getCover_url());
        to.setView_count(from.getView_count());
    }
}
