package com.example.bookmoth.ui.activity.reader;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.adapter.TextItemRecyclerViewAdapter;
import com.example.bookmoth.ui.viewmodel.reader.ReaderChapterListViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChapterListFragment extends Fragment {
    private ReaderChapterListViewModel viewModel;
    private List<String> chapterText;

    private RecyclerView chapterListRV;

    public ChapterListFragment() {
        // Required empty public constructor
    }

    public static ChapterListFragment newInstance() {
        return new ChapterListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reader_chapterlist, container, false);
        chapterListRV = v.findViewById(R.id.rdr_rv_chapterlist);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderChapterListViewModel.class);
        chapterText = new ArrayList<>();
        int k = viewModel.getChapterCounts();
        for (int i=0; i<k; i++) chapterText.add(getString(R.string.chapter_chapter) + " " + (i+1));
        TextItemRecyclerViewAdapter rvAdapter = new TextItemRecyclerViewAdapter(chapterText, pos -> {
            viewModel.setNowChapterIndex(pos);
        });
        chapterListRV.setLayoutManager(new GridLayoutManager(requireContext(), 2, LinearLayoutManager.VERTICAL, false));
        chapterListRV.setAdapter(rvAdapter);
    }
}