package com.example.bookmoth.ui.activity.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.post.PostSQLiteHelper;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.repository.post.FlaskRepositoryImpl;
import com.example.bookmoth.data.repository.post.SupabaseRepositoryImpl;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.domain.usecase.post.FlaskUseCase;
import com.example.bookmoth.domain.usecase.post.PostUseCase;
import com.example.bookmoth.ui.adapter.PostAdapter;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.domain.model.post.Post;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.viewmodel.post.FlaskViewModel;
import com.example.bookmoth.ui.activity.post.CreatePostActivity;
import com.example.bookmoth.ui.activity.profile.ProfileActivity;
import com.example.bookmoth.ui.viewmodel.post.PostViewModel;
import com.example.bookmoth.ui.viewmodel.post.SharedViewModel;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private Button btnLoad;
    private ImageButton btnCreatePost, btnAcc;
    private PostViewModel postViewModel;
    private FlaskViewModel flaskViewModel;
    private PostSQLiteHelper dbHelper;

    private String profileId;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean hasMoreData = true;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        dbHelper = new PostSQLiteHelper(getContext());
        postAdapter = new PostAdapter(getContext(), postList, new PostUseCase(new SupabaseRepositoryImpl()), new FlaskUseCase(new FlaskRepositoryImpl()));
        recyclerView.setAdapter(postAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMoreData && isNetworkAvailable() &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2) {
                    Log.d("Supabase", "Kéo xuống gần cuối, tải trang tiếp theo: " + (currentPage + 1));
                    loadMorePosts();
                }
            }
        });

        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });

        btnAcc = view.findViewById(R.id.button_acc);

        profileId = SecureStorage.getToken("profileId");
        getProfile(() -> {
            loadInitialData(); // chỉ gọi khi profileId đã có
        });
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        flaskViewModel = new FlaskViewModel(new FlaskUseCase(new FlaskRepositoryImpl()));

        // lấy sự kiện click ở button reload homeactivity
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getButtonClicked().observe(getViewLifecycleOwner(), clicked -> {
            if (clicked) {
                resetAndLoadPosts();
            }
        });

        Log.d("Supabase", "HomeFragment onCreateView - Gọi loadInitialData()");
        clickViewProfile();
        return view;
    }

    private void clickViewProfile() {
        btnAcc.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void getProfile(Runnable onSuccess) {
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                getContext(), ProfileDatabase.getInstance(getContext()).profileDao()
        );
        ProfileViewModel profileViewModel = new ProfileViewModel(
                new ProfileUseCase(localRepo, new ProfileRepositoryImpl())
        );

        profileViewModel.getProfile(getContext(), new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                profileViewModel.saveProfile(profile);
                SecureStorage.saveToken("profileId", profile.getProfileId());
                profileId = profile.getProfileId();
                onSuccess.run(); // chỉ gọi load khi xong
            }

            @Override
            public void onProfileFailure(String error) {}
        });
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void loadInitialData() {
        if (isNetworkAvailable()) {
            resetAndLoadPosts();
        } else {
            loadPostsFromSQLite();
            Toast.makeText(getContext(), "Không có kết nối mạng, hiển thị dữ liệu offline", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetAndLoadPosts() {
        currentPage = 0;
        hasMoreData = true;
        postList.clear();
        loadPostsFromServer();
    }

    private void loadMorePosts() {
        if (isLoading || !hasMoreData) return;
        currentPage++;
        loadPostsFromServer();
    }

    private void loadPostsFromServer() {
        if (profileId == null || profileId.isEmpty()) {
            Log.e("loadPosts", "profileId is null or empty");
            return;
        }
        isLoading = true;

        List<Integer> profileIds = new ArrayList<>();
        int profileIdInt = Integer.parseInt(profileId);
        profileIds.add(profileIdInt);

        flaskViewModel.getFollowers(profileIdInt, new FlaskViewModel.OnGetFollowers() {
            @Override
            public void onGetSuccess(List<Integer> followers) {
                profileIds.addAll(followers);
                String profileIdsString = profileIds.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));

                String rangeHeader = String.format("%d-%d", currentPage * PAGE_SIZE, (currentPage + 1) * PAGE_SIZE - 1);
                Log.d("Supabase", "Tải trang: " + currentPage + ", Range: " + rangeHeader);
                postViewModel.getPostsByUserIds("in.(" + profileIdsString + ")", rangeHeader, new PostViewModel.OnGetPost() {
                    @Override
                    public void onGetPostSuccess(List<Post> posts) {
                        isLoading = false;
                        if (posts.size() < PAGE_SIZE) {
                            hasMoreData = false;
                            Log.d("Supabase", "Không còn dữ liệu để tải thêm");
                        }
                        if (currentPage == 0) {
                            postList.clear();
                            // Đếm số bài đã cập nhật profile
                            final int[] updatedCount = {0};
                            final int totalPosts = posts.size();

                            for (Post post : posts) {
                                flaskViewModel.getProfile(post.getAuthorId(), new FlaskViewModel.OnGetProfile() {
                                    @Override
                                    public void onGetSuccess(com.example.bookmoth.domain.model.post.Profile profile) {
                                        post.setAuthor_name(profile.getFirstName() + " " + profile.getLastName());
                                        post.setAuthor_avatar_url("http://127.0.0.1:7100/images/avatars/" + post.getAuthorId() + ".pnj");
                                        updatedCount[0]++;
                                        if (updatedCount[0] == totalPosts) {
                                            saveFirstPageToSQLite(posts);
                                            postList.addAll(posts);
                                            postAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onGetFailure(String message) {
                                        post.setAuthor_name("Unknown");
                                        post.setAuthor_avatar_url(null);
                                        updatedCount[0]++;
                                        if (updatedCount[0] == totalPosts) {
                                            saveFirstPageToSQLite(posts);
                                            postList.addAll(posts);
                                            postAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        } else {
                            postList.addAll(posts);
                            postAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onGetPostFailure(String message) {
                        isLoading = false;
                        if (currentPage == 0) {
                            loadPostsFromSQLite();
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onGetFailure(String message) {
                isLoading = false;
                if (currentPage == 0) {
                    loadPostsFromSQLite();
                }
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostsFromSQLite() {
        postList.clear();
        postList.addAll(dbHelper.getAllPosts());
        postAdapter.notifyDataSetChanged();
    }

    private void saveFirstPageToSQLite(List<Post> posts) {
        dbHelper.savePosts(posts);
    }

}
