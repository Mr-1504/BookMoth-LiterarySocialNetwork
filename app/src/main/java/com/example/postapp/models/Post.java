package com.example.postapp.models;
import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("post_id")
    private int postId;

    @SerializedName("author_id")
    private int authorId;

    private String title;
    private String content;
    private String timestamp;
    private String media_url;
    private String media_type;
    private int count_like;
    private int count_comment;

    public Post() {}

    public Post(int postId, int authorId, String title, String content, String timestamp, String media_url, String media_type, int count_like,int count_comment) {
        this.postId = postId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.media_url = media_url;
        this.media_type = media_type;
        this.count_like = count_like;
        this.count_comment = count_comment;
    }

    public int getPostId() { return postId; }
    public int getAuthorId() { return authorId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public String getMediaUrl() { return media_url; }
    public String getMediaType() { return media_type; }

    public int getCount_like() {
        return count_like;
    }

    public int getCount_comment() {
        return count_comment;
    }
}
