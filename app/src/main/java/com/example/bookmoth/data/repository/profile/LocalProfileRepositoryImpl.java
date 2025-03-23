package com.example.bookmoth.data.repository.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.model.profile.ProfileDao;
import com.example.bookmoth.data.model.profile.ProfileEntity;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.repository.profile.LocalProfileRepository;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalProfileRepositoryImpl implements LocalProfileRepository {
    private final ProfileDao profileDao;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LocalProfileRepositoryImpl(Context context, ProfileDao profileDao) {
        this.context = context;
        this.profileDao = profileDao;
    }

    @Override
    public void saveProfile(Profile profile) {
        Glide.with(context).asBitmap().load(profile.getAvatar()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource,
                                        @Nullable Transition<? super Bitmap> transition) {
                String avatarPath = ImageCache.saveBitmap(context, resource, "avatar.png");

                Glide.with(context).asBitmap().load(profile.getCoverPhoto()).into(
                        new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        String coverPhotoPath = ImageCache.saveBitmap(context, resource, "cover.png");

                        executor.execute(() -> {
                            ProfileEntity entity = new ProfileEntity();
                            entity.profileId = profile.getProfileId();
                            entity.accountId = profile.getAccountId();
                            entity.firstName = profile.getFirstName();
                            entity.lastName = profile.getLastName();
                            entity.username = profile.getUsername();
                            entity.avatar = avatarPath;
                            entity.coverPhoto = coverPhotoPath;
                            entity.dateOfBirth = profile.getDateOfBirth();
                            entity.identifier = profile.isIdentifier();

                            profileDao.saveProfile(profile);
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                })
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        })
    }

    @Override
    public Profile getProfileLocal() {
        ProfileEntity entity = profileDao.getProfileLocal();
        return entity != null ? new Profile(entity) : null;
    }

    @Override
    public void deleteProfileLocal() {
        executor.execute(() ->{
            profileDao.deleteProfileLocal();
            deleteCacheFiles();
        });
    }

    private void deleteCacheFiles() {
        File avatarFile = new File(context.getCacheDir(), "avatar.png");
        if (avatarFile.exists()) {
            avatarFile.delete();
        }

        File coverPhotoFile = new File(context.getCacheDir(), "cover.png");
        if (coverPhotoFile.exists()) {
            coverPhotoFile.delete();
        }
    }
}
