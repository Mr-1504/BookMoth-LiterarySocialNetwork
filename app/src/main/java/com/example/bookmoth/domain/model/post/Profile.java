package com.example.bookmoth.domain.model.post;

import com.google.gson.annotations.SerializedName;

public class Profile {
    @SerializedName("profile_id")
    private int profileId;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("avatar")
    private String avatar;

    public int getProfileId() {
        return profileId;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
