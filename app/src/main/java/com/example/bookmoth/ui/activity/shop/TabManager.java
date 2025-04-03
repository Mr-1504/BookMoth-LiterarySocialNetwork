package com.example.bookmoth.ui.activity.shop;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.ui.activity.home.HomeFragment;
import com.example.bookmoth.ui.adapter.shop.TabPager_Adapter;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class TabManager {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private List<Category> categories;
    private ShopViewModel shopViewModel;
    private TabPager_Adapter pagerAdapter;
    private AppCompatActivity activity;


    public TabManager(AppCompatActivity activity, TabLayout tabLayout, ViewPager2 viewPager, ShopViewModel shopViewModel) {
        this.activity = activity;
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;
        this.categories = new ArrayList<>();
        this.shopViewModel = shopViewModel;
        this.pagerAdapter = new TabPager_Adapter(activity);
        viewPager.setAdapter(pagerAdapter);
        Log.d("TabManager", "TabPager_Adapter set to ViewPager2");
    }

    public void fetchCategories() {
        if (activity == null) {
            Log.e("TabManager", "Activity is null, cannot fetch categories");
            return;
        }
        Log.d("TabManager", "Fetching categories...");

        shopViewModel.getCategories(activity, new ShopViewModel.OnCategoryClickListener() {
            @Override
            public void onSuccess(List<Category> category) {
                categories.clear();
                categories.addAll(category);
                Log.d("TabManager", "Categories received: " + categories.size());

                tabLayout.removeAllTabs();
                pagerAdapter.clear();
                Log.d("TabManager", "Cleared existing tabs and fragments");

                pagerAdapter.addFragment(new HomeFragment(), "Khám phá");
                tabLayout.addTab(tabLayout.newTab().setText("Khám phá"));

                pagerAdapter.addFragment(CategoryFragment.newInstance(new ArrayList<Category>()), "Thể loại");
                tabLayout.addTab(tabLayout.newTab().setText("Thể loại"));

                Log.d("TabManager", "Fragments added to ViewPager2: " + pagerAdapter.getItemCount());
                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(pagerAdapter.getPageTitle(position))
                ).attach();
                Log.d("TabManager", "TabLayoutMediator attached");
                setTabWidth();
                Log.d("API_SUCCESS", "Dữ liệu nhận được: " + category.toString());
            }

            @Override
            public void onFailure(String error) {
                Log.e("TabManager", "Failed to fetch categories: " + error);
            }
        });
    }

    public void setTabWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int numberOfTabsToShow = 2;
        int tabWidth = screenWidth / numberOfTabsToShow;
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            if (tabView != null) {
                tabView.setMinimumWidth(tabWidth);
            } else {
                Log.w("TabManager", "Tab view at position " + i + " is null");
            }
        }
        Log.d("TabManager", "Tab width set");
    }

    public List<Category> getCategories() {
        return categories;
    }
}
