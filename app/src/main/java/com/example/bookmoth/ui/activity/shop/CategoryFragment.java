package com.example.bookmoth.ui.activity.shop;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.ui.adapter.shop.Category_Adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {
    private static final String ARG_CATEGORIES = "categories";
    private RecyclerView rvCategories;
    private List<Category> categories;

    public static CategoryFragment newInstance(ArrayList<Category> categories) {
        CategoryFragment categoryFragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_CATEGORIES, categories);
        categoryFragment.setArguments(args);
        return categoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        rvCategories = view.findViewById(R.id.rv_categories);
        if (rvCategories == null) {
            Log.e("CategoryFragment", "rvCategories is null");
            return view;
        }

        if (getArguments() != null) {
            categories = getArguments().getParcelableArrayList(ARG_CATEGORIES);
            Log.d("CategoryFragment", "Received " + (categories != null ? categories.size() : 0) + " categories");
            if (categories != null) {
                Log.d("CategoryFragment", "Categories: " + categories.toString());
            }
        }
        if (categories == null) {
            categories = new ArrayList<>();
            Log.w("CategoryFragment", "Categories is null, initialized empty list");
        }
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));
        Category_Adapter adapter = new Category_Adapter(categories, getContext(), category -> {
            Intent intent = new Intent(getActivity(), CategoryWorksActivity.class);
            intent.putExtra("category", category);
            startActivity(intent);
        });
        rvCategories.setAdapter(adapter);
        Log.d("CategoryFragment", "RecyclerView adapter set with " + categories.size() + " items");
        return view;
    }
}