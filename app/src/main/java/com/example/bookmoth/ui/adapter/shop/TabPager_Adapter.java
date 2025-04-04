package com.example.bookmoth.ui.adapter.shop;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabPager_Adapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> titleList = new ArrayList<>();

    public TabPager_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        Log.d("TabPager_Adapter", "TabPager_Adapter initialized");
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
        Log.d("TabPager_Adapter", "Added fragment: " + fragment.getClass().getSimpleName() + ", title: " + title);
        notifyDataSetChanged();

    }

    public void clear() {
        fragmentList.clear();
        titleList.clear();
        Log.d("TabPager_Adapter", "Cleared fragments and titles");
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragmentList.get(position);
        Log.d("TabPager_Adapter", "Creating fragment at position " + position + ": " + fragment.getClass().getSimpleName());
        return fragment;

    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
