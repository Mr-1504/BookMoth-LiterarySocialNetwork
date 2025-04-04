package com.example.bookmoth.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bookmoth.R;
import com.example.bookmoth.ui.activity.library.LibTestFragment;
import com.example.bookmoth.ui.activity.library.LibraryMainFragment;
import com.example.bookmoth.ui.activity.option.OptionActivity;
import com.example.bookmoth.ui.activity.option.SettingFragment;
import com.example.bookmoth.ui.activity.shop.ShopActivity;
import com.example.bookmoth.ui.viewmodel.post.SharedViewModel;

public class HomeActivity extends AppCompatActivity {

    private ImageButton buttonHome, buttonBook, buttonStore, buttonNotification, buttonSetting, buttonSearch;
    private SharedViewModel viewModel;
    private TextView tvBookmoth;

    private Fragment libraryMainFragment = new LibraryMainFragment();
    private FrameLayout libraryOwnFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        // Thiết lập xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Trả về insets để tránh lỗi
        });

        // Ánh xạ các nút điều hướng
        buttonHome = findViewById(R.id.button_home);
        buttonBook = findViewById(R.id.button_acc);
        buttonStore = findViewById(R.id.button_store);
        buttonNotification = findViewById(R.id.button_notification);
        buttonSetting = findViewById(R.id.button_setting);
        buttonSearch = findViewById(R.id.button_search);
        tvBookmoth = findViewById(R.id.tvBookmoth2);

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        libraryOwnFrame = findViewById(R.id.library_own_frame);
        libraryOwnFrame.setVisibility(View.GONE);
        initLibraryFragment();

        // Mặc định hiển thị HomeFragment khi mở ứng dụng
        loadFragment(new HomeFragment());

        // Xử lý sự kiện khi bấm các nút
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new HomeFragment());
                buttonHome.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search2));
                buttonBook.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonStore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonNotification.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonSetting.setBackground(ContextCompat.getDrawable(HomeActivity.this,R.color.trans ));
            }
        });
        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new LibTestFragment());
                libraryOwnFrame.setVisibility(View.VISIBLE);
                buttonBook.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search2));
                buttonHome.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonStore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonNotification.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonSetting.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
            }
        });

        buttonStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loadFragment(new StoreFragment());
                buttonStore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search2));
                buttonHome.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonBook.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonNotification.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonSetting.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                Intent intent = new Intent(HomeActivity.this, ShopActivity.class);
                startActivity(intent);

            }
        });

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new NotificationFragment());
                buttonNotification.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search2));
                buttonHome.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonBook.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonStore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
                buttonSetting.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.trans));
            }
        });

        buttonSetting.setOnClickListener(view -> {
            loadFragment(new SettingFragment());
            buttonStore.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search1));
            buttonHome.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search1));
            buttonBook.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search1));
            buttonNotification.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search1));
            buttonSetting.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.search2));
        });

        // Xử lý sự kiện reload
        tvBookmoth.setOnClickListener(v -> {
            viewModel.setButtonClicked();
        });
        buttonSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }


    private void clickOption() {
        buttonSetting.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, OptionActivity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        libraryOwnFrame.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void initLibraryFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.library_own_frame, libraryMainFragment).commit();
    }
}