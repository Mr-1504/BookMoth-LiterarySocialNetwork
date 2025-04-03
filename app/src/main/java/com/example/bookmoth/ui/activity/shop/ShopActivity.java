package com.example.bookmoth.ui.activity.shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabManager tabManager;
    private ShopViewModel shopViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shopViewModel = new ShopViewModel(new ShopUseCase(new ShopRepositoryImpl()));
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        tabManager = new TabManager(this,tabLayout,viewPager,shopViewModel);
        Log.d("MainActivity", "TabManager initialized");
        tabManager.fetchCategories();
        Log.d("MainActivity", "fetchCategories called");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("TabLayout", "Selected tab: " + (tab != null ? tab.getText() : "null"));
                Toast.makeText(ShopActivity.this, "Tab " + (tab != null ? tab.getText() : "null"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d("TabLayout", "Unselected tab: " + (tab != null ? tab.getText() : "null"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d("TabLayout", "Reselected tab: " + (tab != null ? tab.getText() : "null"));
            }
        });
    }
}