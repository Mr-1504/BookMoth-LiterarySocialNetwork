package com.example.bookmoth.ui.activity.shop;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;


import android.Manifest;
import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.activity.home.HomeActivity;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabManager tabManager;
    private ShopViewModel shopViewModel;
    private EditText searchBar;
    private ImageButton btnBack, btnMic, btnSearch;
    private ActivityResultLauncher<Intent> speechToTextLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Toolbar toolbar;
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
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager =(ViewPager2) findViewById(R.id.view_pager);
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnMic = (ImageButton) findViewById(R.id.btn_mic);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        searchBar = (EditText) findViewById(R.id.searchbar);
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
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() == 0) {
                    finish();
                } else {
                    viewPager.setCurrentItem(0);
                }
            }
        });
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                startSpeechToText();
            } else {
                Toast.makeText(this, "Permission denied. Cannot use speech recognition.", Toast.LENGTH_SHORT).show();
            }
        });

        speechToTextLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("ShopActivity", "Speech recognition result received: " + result.getResultCode());
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                ArrayList<String> resultText = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                Log.d("ShopActivity", "Recognized text: " + (resultText != null ? resultText.toString() : "null"));
                if (resultText != null && !resultText.isEmpty()) {
                    String originalText = resultText.get(0);
                    String lowCase = originalText.toLowerCase();
                    String formatText = "";
                    if (!lowCase.isEmpty()) {
                        formatText = lowCase.substring(0, 1).toUpperCase() + lowCase.substring(1);
                    }
                    searchBar.setText(formatText);
                } else {
                    Log.d("ShopActivity", "No speech recognized (empty result)");
                    Toast.makeText(this, "No speech recognized", Toast.LENGTH_SHORT).show();
                }
            } else if (result.getResultCode() == RESULT_CANCELED) {
                Log.d("ShopActivity", "Speech recognition canceled by user or failed");
                Toast.makeText(this, "Speech recognition canceled", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("ShopActivity", "Speech recognition failed with result code: " + result.getResultCode());
                Toast.makeText(this, "Speech recognition failed", Toast.LENGTH_SHORT).show();
            }
        });
        btnMic.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection. Speech recognition requires internet.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                startSpeechToText();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            }
        });
        btnSearch.setOnClickListener(v -> {
            String query = searchBar.getText().toString();
            if (!query.isEmpty()) {
                Intent intent = new Intent(ShopActivity.this, SearchWorkActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void startSpeechToText() {
        Log.d("ShopActivity", "Starting speech recognition...");
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Log.e("ShopActivity", "Speech recognition not supported on this device");
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //  intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        try {
            Log.d("ShopActivity", "Launching speech recognition intent...");
            speechToTextLauncher.launch(intent);
        } catch (Exception e) {
            Log.e("ShopActivity", "Error starting speech recognition", e);
            Toast.makeText(this, "Error starting speech recognition: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}