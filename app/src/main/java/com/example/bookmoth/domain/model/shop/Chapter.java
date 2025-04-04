package com.example.bookmoth.domain.model.shop;

import android.os.Parcel;
import android.os.Parcelable;

public class Chapter implements Parcelable {
    private int chapter_id;
    private int work_id;
    private String title;
    private String post_date;
    private String content_url;

    public Chapter(int chapter_id, int work_id, String title, String post_date, String content_url) {
        this.chapter_id = chapter_id;
        this.work_id = work_id;
        this.title = title;
        this.post_date = post_date;
        this.content_url = content_url;
    }
    protected Chapter(Parcel in) {
        chapter_id = in.readInt();
        work_id = in.readInt();
        title = in.readString();
        post_date = in.readString();
        content_url = in.readString();
    }
    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel in) {
            return new Chapter(in);
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chapter_id);
        dest.writeInt(work_id);
        dest.writeString(title);
        dest.writeString(post_date);
        dest.writeString(content_url);
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getWork_id() {
        return work_id;
    }

    public void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }
}
