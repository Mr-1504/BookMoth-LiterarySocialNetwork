package com.example.bookmoth.domain.model.library;

import java.io.Serializable;

public class Chapter implements Serializable {
    private int chapter_id;
    private int work_id;
    private String title;
    private String post_date;
    private String content_url;

    public Chapter() {}

    public int getChapter_id() {
        return chapter_id;
    }

    public int getWork_id() {
        return work_id;
    }

    public String getTitle() {
        return title;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setTitle(String title) {this.title = title;}

    public void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }
}
