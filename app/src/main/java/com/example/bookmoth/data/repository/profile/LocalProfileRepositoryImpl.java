package com.example.bookmoth.data.repository.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.local.profile.ProfileDao;
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
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(profile.getAvatar())
                .diskCacheStrategy(DiskCacheStrategy.NONE) // Không dùng cache để kiểm tra ảnh mới nhất
                .skipMemoryCache(true)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("Glide", "Failed to load avatar", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model,
                                                   Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("Glide", "Avatar loaded - Width: " + resource.getWidth() + " Height: " + resource.getHeight());
                        return false;
                    }
                })
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        Log.d("Glide", "Avatar Bitmap received - Width: " + resource.getWidth() + " Height: " + resource.getHeight());

                        ImageCache.deleteBitmap(context, "avatar.png");
                        String avatarPath = ImageCache.saveBitmap(
                                context.getApplicationContext(), resource, "avatar.png");
                        Log.d("saveProfile", "Avatar saved at: " + avatarPath);

                        Glide.with(context.getApplicationContext())
                                .asBitmap()
                                .load(profile.getCoverphoto())
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                Target<Bitmap> target, boolean isFirstResource) {
                                        Log.e("Glide", "Failed to load cover photo", e);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model,
                                                                   Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.d("Glide", "Cover Photo loaded - Width: " + resource.getWidth() + " Height: " + resource.getHeight());
                                        return false;
                                    }
                                })
                                .into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource,
                                                                @Nullable Transition<? super Bitmap> transition) {
                                        Log.d("Glide", "Cover Photo Bitmap received - Width: " + resource.getWidth() + " Height: " + resource.getHeight());

                                        ImageCache.deleteBitmap(context, "cover.png");
                                        String coverPhotoPath = ImageCache.saveBitmap(
                                                context.getApplicationContext(), resource, "cover.png");
                                        Log.d("saveProfile", "Cover photo saved at: " + coverPhotoPath);

                                        executor.execute(() -> {
                                            ProfileEntity entity = new ProfileEntity(
                                                    profile.getProfileId(),
                                                    profile.getAccountId(),
                                                    profile.getFirstName(),
                                                    profile.getLastName(),
                                                    profile.getUsername(),
                                                    avatarPath,
                                                    coverPhotoPath,
                                                    profile.getGender(),
                                                    profile.isIdentifier(),
                                                    profile.getBirth()
                                            );

                                            profileDao.saveProfile(entity);
                                        });
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        Log.d("saveProfile", "Cover photo load cleared");
                                    }
                                });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("saveProfile", "Avatar load cleared");
                    }
                });
    }


    @Override
    public Profile getProfileLocal() {
        ProfileEntity entity = profileDao.getProfileLocal();
        return entity != null ? new Profile(entity) : null;
    }

    @Override
    public void deleteProfileLocal() {
        executor.execute(() -> {
            profileDao.deleteProfileLocal();
            deleteCacheFiles();
        });
    }

    @Override
    public boolean isProfileExist() {
        return getProfileLocal() != null;
    }

    private void deleteCacheFiles() {
        Log.i("DELETE PROFILE", "Deleting cache files");
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
