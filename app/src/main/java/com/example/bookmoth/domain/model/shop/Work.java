package com.example.bookmoth.domain.model.shop;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Work implements Parcelable {
    private int work_id;
    private int profile_id;
    private String title;
    private String post_date;
    private BigDecimal price;
    private BigInteger view_count;
    private String description;
    private String cover_url;

    public Work(int work_id, int profile_id, String title, String post_date, BigDecimal price, BigInteger view_count, String description, String cover_url) {
        this.work_id = work_id;
        this.profile_id = profile_id;
        this.title = title;
        this.post_date = post_date;
        this.price = price;
        this.view_count = view_count;
        this.description = description;
        this.cover_url = cover_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(work_id);
        dest.writeInt(profile_id);
        dest.writeString(title);
        dest.writeString(post_date);
        dest.writeString(price.toString());
        dest.writeString(view_count.toString());
        dest.writeString(description.toString());
        dest.writeString(cover_url.toString());
    }

    protected Work(Parcel in){
        work_id = in.readInt();
        profile_id = in.readInt();
        title = in.readString();
        post_date = in.readString();
        price = new BigDecimal(in.readString());
        view_count =  new BigInteger(in.readString());
        description = in.readString();
        cover_url = in.readString();
    }
    public static final Creator<Work> CREATOR = new Creator<Work>() {
        @Override
        public Work createFromParcel(Parcel in) {
            return new Work(in);
        }

        @Override
        public Work[] newArray(int size) {
            return new Work[size];
        }
    };

    public int getWork_id() {
        return work_id;
    }

    public void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigInteger getView_count() {
        return view_count;
    }

    public void setView_count(BigInteger view_count) {
        this.view_count = view_count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover_url() {
        return cover_url;
    }

    public void setCover_url(String cover_url) {
        this.cover_url = cover_url;
    }
}
