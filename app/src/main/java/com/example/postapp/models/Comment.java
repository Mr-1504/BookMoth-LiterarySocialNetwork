package com.example.postapp.models;

import com.google.gson.annotations.SerializedName;

public class Comment {
    @SerializedName("id_comment")
    private int id;

    @SerializedName("user_id")
    private int user_id;

    @SerializedName("post_id")
    private int post_id;

    @SerializedName("count_like")
    private int count_like;

    private String time;
    private String content;

    public Comment(){};

    public Comment(int id, int user_id, int post_id, int count_like, String time, String content){
        this.id = id;
        this.user_id = user_id;
        this.post_id = post_id;
        this.count_like = count_like;
        this.time = time;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public int getCount_like() {
        return count_like;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }
}
