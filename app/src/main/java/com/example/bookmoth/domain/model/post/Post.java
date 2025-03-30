package com.example.bookmoth.domain.model.post;
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
    private int tab_works;
    private String author_name;
    private String author_avatar_url;


    public Post() {}

    public Post(int postId, int authorId, String title, String content, String timestamp, String media_url, String media_type, int count_like,int count_comment, int tab_works, String author_name, String author_avatar_url) {
        this.postId = postId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.media_url = media_url;
        this.media_type = media_type;
        this.count_like = count_like;
        this.count_comment = count_comment;
        this.tab_works = tab_works;
        this.author_name = author_name;
        this.author_avatar_url = author_avatar_url;
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
    public int getTab_works() {
        return tab_works;
    }

    public String getAuthor_name() {
        return author_name;
    }
    public String getAuthor_avatar_url() {
        return author_avatar_url;
    }
    public void setPostId(int postId) { this.postId = postId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setMediaUrl(String media_url) { this.media_url = media_url; }
    public void setMediaType(String media_type) { this.media_type = media_type; }
    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }
    public void setAuthor_avatar_url(String author_avatar_url) {
        this.author_avatar_url = author_avatar_url;
    }
}
