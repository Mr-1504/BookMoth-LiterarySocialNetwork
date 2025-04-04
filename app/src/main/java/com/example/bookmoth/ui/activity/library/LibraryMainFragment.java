package com.example.bookmoth.ui.activity.library;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.ui.viewmodel.worklist.LibraryWorkViewModel;

public class LibraryMainFragment extends Fragment {
    LibraryWorkViewModel viewModel;
    FragmentManager fragmentManager;
    Fragment readerFragment;
    Fragment authorFragment;
    View view;

    MaterialButton toggleMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_main, container, false);
        this.view = view;
        initObjects();
        initFunctions();
        initLiveData();
        changeListMode(MODE_READER);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(requireActivity()).get(LibraryWorkViewModel.class);

        fragmentManager = getChildFragmentManager();
        for (Fragment f : fragmentManager.getFragments()) fragmentManager.beginTransaction().remove(f).commit();
        readerFragment = new ReaderFragment();
        authorFragment = new AuthorFragment();
        fragmentManager.beginTransaction()
                .add(R.id.lib_frame_main, readerFragment)
                .hide(readerFragment)
                .add(R.id.lib_frame_main, authorFragment)
                .hide(authorFragment)
                .commit();

        toggleMode = view.findViewById(R.id.lib_btnSwitchMode_main);
    }

    private void initFunctions() {
        toggleMode.setOnClickListener(v -> {
            changeListMode(MODE_TOGGLE);
        });
    }

    private void initLiveData() {
        viewModel.getErrorString().observe(requireActivity(), v -> ErrorDialog.showError(requireContext(), v));
    }

    private final int MODE_TOGGLE = 0;
    private final int MODE_READER = 1;
    private final int MODE_AUTHOR = 2;
    private void changeListMode(int switchMode) {
        switch (switchMode) {
            case MODE_TOGGLE:
                switchMode = toggleMode.getTag() == "reader" ? MODE_AUTHOR : MODE_READER;
                changeListMode(switchMode);
                break;
            case MODE_READER:
                changeFragment(readerFragment);
                changeActivityVisual(switchMode);
                break;
            case MODE_AUTHOR:
                changeFragment(authorFragment);
                changeActivityVisual(switchMode);
                break;
        }
    }

    private void changeFragment(Fragment frag) {
        fragmentManager.beginTransaction()
                .hide(readerFragment)
                .hide(authorFragment)
                .commit();
        fragmentManager.beginTransaction().show(frag).commit();
    }

    private void changeActivityVisual(int switchMode) {
        switch (switchMode) {
            case MODE_READER:
                toggleMode.setText(R.string.libmode_toAuthor);
                toggleMode.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_edit_square_24));
                toggleMode.setTag("reader");
                break;
            case MODE_AUTHOR:
                toggleMode.setText(R.string.libmode_toReader);
                toggleMode.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.baseline_menu_book_24));
                toggleMode.setTag("author");
                break;
        }
    }
}