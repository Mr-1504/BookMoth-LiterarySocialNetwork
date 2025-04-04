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
import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.adapter.AuthorPageRecyclerViewAdapter;
import com.example.bookmoth.ui.activity.authorcrud.AddChapterActivity;
import com.example.bookmoth.ui.activity.authorcrud.AddWorkActivity;
import com.example.bookmoth.ui.activity.workdetails.WorkDashboardActivity;
import com.example.bookmoth.ui.viewmodel.worklist.LibraryWorkViewModel;

import java.util.ArrayList;
import java.util.List;

public class AuthorFragment extends Fragment {
    LibraryWorkViewModel viewModel;
    View view;
    List<Work> works = new ArrayList<>();
    RecyclerView rv_works;
    GridLayoutManager rv_layoutManager;
    AuthorPageRecyclerViewAdapter rv_works_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_author, container, false);
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
        if (!hidden) viewModel.fetchCreatedWorks();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryWorkViewModel.class);

        works = new ArrayList<Work>();

        rv_works = view.findViewById(R.id.lib_rv_writelist);
        rv_layoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
        doLayoutConfig();
        rv_works.setLayoutManager(rv_layoutManager);

        rv_works_adapter = new AuthorPageRecyclerViewAdapter(works);
        rv_works_adapter.attachAddWorkListener(pos -> {
            Intent createWork = new Intent(requireContext(), AddWorkActivity.class);
            startActivity(createWork);
        });
        rv_works_adapter.attachAddChapteristener(pos -> {
            Intent addChapter = new Intent(requireContext(), AddChapterActivity.class);
            Bundle req = AddChapterActivity.makeRequirementBundle(works);
            addChapter.putExtra("requirement", req);
            startActivity(addChapter);
        });
        rv_works_adapter.attachWorkClickListener(pos -> {
            Intent workDash = new Intent(requireContext(), WorkDashboardActivity.class);
            workDash.putExtra("work_id", works.get(pos-1).getWork_id());
            startActivity(workDash);
        });
        rv_works_adapter.attachRefreshListener(view -> viewModel.fetchCreatedWorks());
        rv_works.setAdapter(rv_works_adapter);
    }

    private void doLayoutConfig() {
        rv_layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == AuthorPageRecyclerViewAdapter.VIEWTYPE_QUICKACTION || position == AuthorPageRecyclerViewAdapter.VIEWTYPE_HEADER) return 3;
                return 1;
            }
        });
    }

    private void initLiveData() {
        viewModel.getCreatedWorks().observe(requireActivity(), v -> {
            works.clear();
            works.addAll(v);
            rv_works_adapter.notifyDataSetChanged();
        });
    }
}
