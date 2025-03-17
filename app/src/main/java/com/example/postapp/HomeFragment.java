package com.example.postapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.postapp.adapters.PostAdapter;
import com.example.postapp.api.ApiClient;
import com.example.postapp.models.Post;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList = new ArrayList<>();
    private Button btnLoad;
    private ImageButton btnCreatePost;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        recyclerView = view.findViewById(R.id.recyclerViewComments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        btnCreatePost = view.findViewById(R.id.btnCreatePost);
        btnCreatePost.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreatePostActivity.class);
            startActivity(intent);
        });
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

        return view;
    }

    private void loadPosts() {
        ApiClient.getDatabaseApi().getPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    Log.d("Supabase", "Số lượng bài viết: " + posts.size());

                    postList.clear();
                    postList.addAll(posts);
                    postAdapter.notifyDataSetChanged();
                } else {
                    Log.e("Supabase", "Lỗi lấy bài viết: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("Supabase", "Lỗi kết nối API", t);
            }
        });
    }
}
