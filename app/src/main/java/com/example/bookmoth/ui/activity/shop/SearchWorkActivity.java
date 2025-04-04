package com.example.bookmoth.ui.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.activity.home.HomeActivity;
import com.example.bookmoth.ui.adapter.shop.WorkHoriz_Adapter;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchWorkActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvNoResults;
    private WorkHoriz_Adapter workAdapter;
    private List<Work> workList;
    private ShopViewModel shopViewModel;
    private Toolbar toolbar;
    private ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_work);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_work), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn tiêu đề mặc định
        }
        btnBack =(ImageButton) findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_search_results);
        tvNoResults = findViewById(R.id.tv_no_results);
        workList = new ArrayList<>();
        workAdapter = new WorkHoriz_Adapter(workList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(workAdapter);
        shopViewModel = new ShopViewModel(new ShopUseCase(new ShopRepositoryImpl()));

        String searchQuery = getIntent().getStringExtra("query");
        if (searchQuery != null &&  !searchQuery.isEmpty()) {
            searchWorks(searchQuery);
        } else {
            tvNoResults.setText("Không có từ khóa tìm kiếm");
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchWorkActivity.this, ShopActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void searchWorks(String title) {
        shopViewModel.getWorkByTitle(this, title, new ShopViewModel.OnGetWorkByTitleListener() {
            @Override
            public void onSuccess(List<Work> work) {
                workList.clear();
                workList.addAll(work);
                if(workList.isEmpty()) {
                    tvNoResults.setVisibility(View.VISIBLE);
                    tvNoResults.setText("Không có kết quả nào cho từ khóa này");
                    recyclerView.setVisibility(View.GONE);
                } else {
                    workAdapter.notifyDataSetChanged();
                    tvNoResults.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SearchWorkActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                tvNoResults.setVisibility(View.VISIBLE);
                tvNoResults.setText("Lỗi khi tìm kiếm");
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}