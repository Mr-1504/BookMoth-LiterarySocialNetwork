package com.example.bookmoth.domain.model.profile;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    private int profile_Id;
    @SerializedName("username")
    private String username;
    @SerializedName("first_Name")
    private String first_name;
    @SerializedName("last_Name")
    private String last_name;
    private String avatar;
    private int mutualCount;
    private int followers;
    private int following;
    private int followed;

    public String getAvatar() {
        return avatar;
    }

    public String getFirst_name() {
        return first_name;
    }

    public int getFollowed() {
        return followed;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public String getLast_name() {
        return last_name;
    }

    public int getMutualCount() {
        return mutualCount;
    }

    public int getProfile_Id() {
        return profile_Id;
    }

    public String getUsername() {
        return username;
    }

    public void setFollowed(int i) {
        followed = i;
    }
}
