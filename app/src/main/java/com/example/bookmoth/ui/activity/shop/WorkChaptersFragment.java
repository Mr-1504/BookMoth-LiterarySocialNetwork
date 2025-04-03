package com.example.bookmoth.ui.activity.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.ui.adapter.shop.Chapter_Adapter;

import java.util.ArrayList;
import java.util.List;

public class WorkChaptersFragment extends Fragment implements OnChapterClickListener {
    private static final String ARG_WORK_ID = "work_id";
    private RecyclerView rvChapters;
    private List<Chapter> chapterList;
    private Chapter_Adapter chapterAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chapterList = new ArrayList<>();
        chapterAdapter = new Chapter_Adapter(chapterList);
        chapterAdapter.setOnChapterClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_chapters, container,false);

        rvChapters = view.findViewById(R.id.rv_chapters);
        rvChapters.setLayoutManager( new LinearLayoutManager(getContext()));
        rvChapters.setAdapter(chapterAdapter);

        if(getArguments() != null) {
            int workId = getArguments().getInt(ARG_WORK_ID);

        }
        return  view;
    }

    @Override
    public void onChapterClick(Chapter chapter) {

    }

    public static WorkChaptersFragment newInstance(int workId) {
        WorkChaptersFragment  fragment = new WorkChaptersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WORK_ID, workId);
        fragment.setArguments(args);
        return fragment;
    }
}
