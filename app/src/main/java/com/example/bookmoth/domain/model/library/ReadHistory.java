package com.example.bookmoth.domain.model.library;

public class ReadHistory {
    private int chapter_id;
    private int work_id;
    private String post_date;

    public ReadHistory(int chapter_id, int work_id, String post_date) {
        this.chapter_id = chapter_id;
        this.work_id = work_id;
        this.post_date = post_date;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public int getWork_id() {
        return work_id;
    }

    public String getPost_date() {
        return post_date;
    }
}
