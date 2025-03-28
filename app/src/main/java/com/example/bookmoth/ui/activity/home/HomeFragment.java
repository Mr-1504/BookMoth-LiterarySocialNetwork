package com.example.bookmoth.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.SecureStorage;
import com.example.bookmoth.data.local.profile.ProfileDatabase;
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

    private String profileId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postAdapter = new PostAdapter(getContext(), postList, new PostUseCase(new SupabaseRepositoryImpl()), new FlaskUseCase(new FlaskRepositoryImpl()));
        recyclerView.setAdapter(postAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Xử lý thêm nếu cần
            }
        });

        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });

        btnAcc = view.findViewById(R.id.button_acc);

        profileId = SecureStorage.getToken("profileId");
        getProfile();
        postViewModel = new PostViewModel(new PostUseCase(new SupabaseRepositoryImpl()));
        flaskViewModel = new FlaskViewModel(new FlaskUseCase(new FlaskRepositoryImpl()));

        // lấy sự kiện click ở button reload homeactivity
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getButtonClicked().observe(getViewLifecycleOwner(), clicked -> {
            if (clicked) {
                loadPosts();
            }
        });
//        btnLoad = view.findViewById(R.id.btnLoad);
//        btnLoad.setOnClickListener(v -> {
//            loadPosts();
//        });

        Log.d("Supabase", "HomeFragment onCreateView - Gọi loadPosts()");
        loadPosts();
        clickViewProfile();
        return view;
    }

    private void clickViewProfile() {
        btnAcc.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void getProfile() {
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
            }

            @Override
            public void onProfileFailure(String error) {
            }
        });
    }


    private void loadPosts() {
        List<Integer> profile_ids = new ArrayList<>();
        profileId = SecureStorage.getToken("profileId");
        getProfile();
        Log.d("loadPosts", "profileId = " + profileId);
        if (profileId == null || profileId.isEmpty()) {
            Log.e("loadPosts", "profileId is null or empty");
            return;
        }
        int profileIdInt = Integer.parseInt(profileId);
        profile_ids.add(profileIdInt);
        flaskViewModel.getFollowers(Integer.parseInt(profileId), new FlaskViewModel.OnGetFollowers() {
            @Override
            public void onGetSuccess(List<Integer> followers) {
                profile_ids.addAll(followers);
                String profileIdsString = profile_ids.stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                postViewModel.getPostByIdUser("in.(" + profileIdsString + ")", new PostViewModel.OnGetPost() {
                    @Override
                    public void onGetPostSuccess(List<Post> posts) {
                        postList.clear();
                        postList.addAll(posts);
                        postAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onGetPostFailure(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onGetFailure(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
