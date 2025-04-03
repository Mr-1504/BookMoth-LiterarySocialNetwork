package com.example.bookmoth.domain.model.shop;

import android.os.Parcel;
import android.os.Parcelable;

public class Profile implements Parcelable {
    private int profile_id;
    private int work_id;
    private String first_name;
    private String last_name;
    private String username;
    private String avatar;
    private String coverphoto;
    private boolean identifier;
    private int gender;

    public Profile(int profile_id, int work_id, String first_name, String last_name, String username, String avatar, String coverphoto, boolean identifier, int gender) {
        this.profile_id = profile_id;
        this.work_id = work_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.avatar = avatar;
        this.coverphoto = coverphoto;
        this.identifier = identifier;
        this.gender = gender;
    }

    protected Profile(Parcel in) {
        profile_id = in.readInt();
        work_id = in.readInt();
        first_name = in.readString();
        last_name = in.readString();
        username = in.readString();
        avatar = in.readString();
        coverphoto = in.readString();
        identifier = in.readByte() != 0;
        gender = in.readInt();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(profile_id);
        dest.writeInt(work_id);
        dest.writeString(first_name);
        dest.writeString(last_name);
        dest.writeString(username);
        dest.writeString(avatar);
        dest.writeString(coverphoto);
        dest.writeByte((byte) (identifier ? 1: 0));
        dest.writeInt(gender);
    }

    public int getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(int profile_id) {
        this.profile_id = profile_id;
    }

    public int getWork_id() {
        return work_id;
    }

    public void setWork_id(int work_id) {
        this.work_id = work_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCoverphoto() {
        return coverphoto;
    }

    public void setCoverphoto(String coverphoto) {
        this.coverphoto = coverphoto;
    }

    public boolean getIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
