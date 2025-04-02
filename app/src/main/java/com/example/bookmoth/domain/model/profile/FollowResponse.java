package com.example.bookmoth.domain.model.profile;

import com.google.gson.annotations.SerializedName;

public class FollowResponse {
    @SerializedName("isFollowing")
    private boolean isFollowing;

    public boolean isFollowing() {
        return isFollowing;
    }
}
