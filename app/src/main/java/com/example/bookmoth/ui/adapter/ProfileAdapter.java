package com.example.bookmoth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookmoth.R;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.profile.ProfileResponse;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.activity.profile.ProfileActivity;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private final Context context;
    private final List<ProfileResponse> profiles;
    private final ProfileViewModel profileViewModel;

    public ProfileAdapter(@NonNull Context context, @NonNull List<ProfileResponse> profiles) {
        this.context = context;
        this.profiles = profiles;

        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                context, ProfileDatabase.getInstance(context).profileDao()
        );
        profileViewModel = new ProfileViewModel(new ProfileUseCase(localRepo, new ProfileRepositoryImpl()));
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_profiles, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileResponse profileResponse = profiles.get(position);

        if (profileResponse != null) {

            Glide.with(context)
                    .load(profileResponse.getAvatar())
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(200, 200)
                    .thumbnail(0.1f)
                    .into(holder.avatar);
            holder.txtName.setText(String.format("%s %s", profileResponse.getLast_name(), profileResponse.getFirst_name()));

            String strFollower = profileResponse.getFollowers() > 0 ?
                    String.format("%s %s", profileResponse.getFollowers(), context.getString(R.string.follower)) : "";
            holder.follower.setText(strFollower);

            String strMutualFriend = profileResponse.getMutualCount() > 0 ?
                    String.format("%s %s", profileResponse.getMutualCount(), context.getString(R.string.mutual_friend)) : "";
            holder.mutualFriend.setText(strMutualFriend);

            if (profileResponse.getFollowed() == 1) {
                holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCDADA")));
                holder.follow.setText(context.getString(R.string.following));
                holder.follow.setTextColor(Color.BLACK);
            } else {
                holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e8f4fc")));
                holder.follow.setText(context.getString(R.string.follow));
                holder.follow.setTextColor(ColorStateList.valueOf(Color.parseColor("#30577c")));
            }

            holder.itemView.setOnClickListener(view ->{
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("profileId", String.valueOf(profileResponse.getProfile_Id()));
                context.startActivity(intent);
            });

            follow(holder, profileResponse);
        }
    }

    private void follow(ProfileViewHolder holder, ProfileResponse profileResponse) {
        holder.follow.setOnClickListener(view -> {

            if (profileResponse.getFollowed() == 1) {
                holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e8f4fc")));
                holder.follow.setText(context.getString(R.string.follow));
                holder.follow.setTextColor(ColorStateList.valueOf(Color.parseColor("#30577c")));

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    profileViewModel.unfollow(context, String.valueOf(profileResponse.getProfile_Id()), new ProfileViewModel.OnFollowProfile() {
                        @Override
                        public void onSuccess() {
                            profileResponse.setFollowed(0);
                        }

                        @Override
                        public void onError(String error) {
                            holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCDADA")));
                            holder.follow.setText(context.getString(R.string.following));
                            holder.follow.setTextColor(Color.BLACK);
                        }
                    });
                }, 600);

            } else {
                holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCDADA")));
                holder.follow.setText(context.getString(R.string.following));
                holder.follow.setTextColor(Color.BLACK);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    profileViewModel.follow(context, String.valueOf(profileResponse.getProfile_Id()), new ProfileViewModel.OnFollowProfile() {
                        @Override
                        public void onSuccess() {
                            profileResponse.setFollowed(0);
                        }

                        @Override
                        public void onError(String error) {
                            holder.follow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e8f4fc")));
                            holder.follow.setText(context.getString(R.string.follow));
                            holder.follow.setTextColor(ColorStateList.valueOf(Color.parseColor("#30577c")));
                        }
                    });
                }, 600);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView follower;
        TextView mutualFriend;
        MaterialButton follow;
        ImageView avatar;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.profileName);
            follower = itemView.findViewById(R.id.txtFollower);
            mutualFriend = itemView.findViewById(R.id.mutual_friend);
            follow = itemView.findViewById(R.id.button_follow);
            avatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
