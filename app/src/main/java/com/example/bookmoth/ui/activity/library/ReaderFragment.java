package com.example.bookmoth.ui.activity.library;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.adapter.ReaderPageRecyclerViewAdapter;
import com.example.bookmoth.ui.viewmodel.worklist.LibraryWorkViewModel;
import com.example.bookmoth.ui.activity.workdetails.WorkDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ReaderFragment extends Fragment {
    LibraryWorkViewModel viewModel;
    View view;
    List<Work> works = new ArrayList<>();
    RecyclerView rv_works;
    GridLayoutManager rv_layoutManager;
    ReaderPageRecyclerViewAdapter rv_works_adapter;

    private int gridColSize = 3;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        initObjects();
        initLiveData();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) viewModel.fetchOwnedWorks();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryWorkViewModel.class);

        rv_works = view.findViewById(R.id.lib_rv_writelist);
        rv_layoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        rv_layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? gridColSize : 1;
            }
        });
        rv_works.setLayoutManager(rv_layoutManager);
        rv_works_adapter = new ReaderPageRecyclerViewAdapter(
                works,
                pos -> {
                    Intent detailsActiv = new Intent(getContext(), WorkDetailActivity.class);
                    detailsActiv.putExtra("work", works.get(pos - 1));
                    startActivity(detailsActiv);
                },
                view -> {
                    viewModel.fetchOwnedWorks();
                },
                66);
        rv_works.setAdapter(rv_works_adapter);
    }

    private void initLiveData() {
        viewModel.getOwnedWorks().observe(requireActivity(), v -> {
            works.clear();
            works.addAll(v);
            rv_works_adapter.notifyDataSetChanged();
        });
    }
}
