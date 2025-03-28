package com.example.bookmoth.ui.activity.profile;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.adapter.PostAdapter;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;
    private TextView txtFollower, txtName, txtUsername, txtBack;
    private ImageView avatar, coverPhoto;
    private PostViewModel postViewModel;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private RecyclerView content;
    private String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadPostProfileID();
        clickReturn();
    }

    private void clickReturn() {
        txtBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initViews() {
        txtBack = findViewById(R.id.txtBack);
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        postAdapter = new PostAdapter(this, postList, new PostUseCase(
                new SupabaseRepositoryImpl()), new FlaskUseCase(new FlaskRepositoryImpl())
        );

        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        profileViewModel = new ProfileViewModel(new ProfileUseCase(localRepo, new ProfileRepositoryImpl()));
        txtFollower = findViewById(R.id.txtFollower);
        txtName = findViewById(R.id.txtName);
        avatar = findViewById(R.id.imageViewAvatar);
        coverPhoto = findViewById(R.id.imageViewCover);
        txtUsername = findViewById(R.id.txUsername);
        content = findViewById(R.id.contentRecyclerView);
        content.setLayoutManager(new LinearLayoutManager(this));
        content.setAdapter(postAdapter);

        profileId = getIntent().getStringExtra("profileId");
        if (profileId != null) {
            getProfileById(profileId);
        } else {
            profileId = SecureStorage.getToken("profileId");
            getMe();
        }
    }

    private void getProfileById(String profileId) {
        profileViewModel.getProfileById(this, profileId, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                runOnUiThread(() -> {
                    setProfile(profile, false);
                });
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMe() {
        profileViewModel.isProfileExist(exist -> {
            if (exist) {
                profileViewModel.getProfileLocal(new ProfileViewModel.OnProfileListener() {
                    @Override
                    public void onProfileSuccess(Profile profile) {
                        setProfile(profile, true);
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
                    @Override
                    public void onProfileSuccess(Profile profile) {
                        if (profile == null) return;
                        runOnUiThread(() -> {
                            profileViewModel.saveProfile(profile);
                            setProfile(profile, false);
                        });
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setProfile(Profile profile, boolean isExist) {
        // Set text
        txtName.setText(String.format("%s %s",
                profile.getLastName(), profile.getFirstName()));

        txtFollower.setText(String.format("%s %s", 0, getString(R.string.follower)));

        txtUsername.setText(String.format("(%s)", profile.getUsername()));

        if (isExist) {
            if (ImageCache.isBitmapExists(this, "avatar.png")) {
                Bitmap avatarBitmap = ImageCache.loadBitmap(this, "avatar.png");
                avatar.setImageBitmap(avatarBitmap);
            }
            if (ImageCache.isBitmapExists(this, "cover.png")) {
                Bitmap coverBitmap = ImageCache.loadBitmap(this, "cover.png");
                coverPhoto.setImageBitmap(coverBitmap);
            }
            return;
        }
        // Load Avatar
        Glide.with(ProfileActivity.this)
                .load(profile.getAvatar())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200) // Giảm kích thước ảnh để tăng tốc
                .thumbnail(0.1f) // Load ảnh nhỏ trước để hiển thị nhanh hơn
                .into(avatar);

        // Load Cover Photo
        Glide.with(ProfileActivity.this)
                .load(profile.getCoverphoto())
                .placeholder(R.drawable.avatar)
                .error(R.drawable.cover)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(800, 400) // Kích thước hợp lý hơn cho ảnh bìa
                .thumbnail(0.1f)
                .into(coverPhoto);
    }

    private void loadPostProfileID() {
        postViewModel.getPostByIdUser("eq." + profileId, new PostViewModel.OnGetPost() {
            @Override
            public void onGetPostSuccess(List<Post> posts) {
                postList.clear();
                postList.addAll(posts);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onGetPostFailure(String message) {
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
