package com.example.bookmoth.ui.activity.reader;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.viewmodel.reader.ReaderScrollViewModel;

import java.util.Locale;

public class ScrollFragment extends Fragment {
    private ReaderScrollViewModel viewModel;

    private int activity_height = 1;

    private SeekBar scrollBar;
    private TextView scrolledPage;
    private int nowPage;
    private int maxPage;

    public ScrollFragment() {}

    public static ScrollFragment newInstance() {
        return new ScrollFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reader_scroll, container, false);
        scrollBar = v.findViewById(R.id.rdr_sb_scroll);
        scrolledPage = v.findViewById(R.id.rdr_tv_scroll);
        nowPage = 1;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ReaderScrollViewModel.class);
        initInner();
        initObservers();
    }

    private void initObservers() {
        viewModel.getActivityHeight().observe(getViewLifecycleOwner(), value -> activity_height = value);
        viewModel.getContentHeight().observe(getViewLifecycleOwner(), v -> {
            scrollBar.setMax(v);
            maxPage = (v / activity_height) + 1;
            scrolledPage.setText(String.format(Locale.getDefault(), "%d/%d", nowPage, maxPage));
        });
        viewModel.getViewScrollPosition().observe(getViewLifecycleOwner(), scrollBar::setProgress);
    }

    private void initInner() {
        scrolledPage.setVisibility(View.GONE);
        scrollBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if ((i / activity_height) + 1 != nowPage) {
                    nowPage = (i / activity_height) + 1;
                    scrolledPage.setText(String.format(Locale.getDefault(), "%d/%d", nowPage, maxPage));
                }
                viewModel.setBarScrollPosition(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                scrolledPage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                scrolledPage.setVisibility(View.GONE);
            }
        });
    }
}