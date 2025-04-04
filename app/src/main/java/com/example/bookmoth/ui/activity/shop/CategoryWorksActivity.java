package com.example.bookmoth.ui.activity.shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.model.shop.WorkResponse;
import com.example.bookmoth.domain.repository.shop.ShopRepository;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.adapter.shop.WorkHoriz_Adapter;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryWorksActivity extends AppCompatActivity {

    private RecyclerView rvWorks;
    private TextView tvCategoryTag;
    private Button btnPrevious, btnNext;
    private List<Work> workList;
    private WorkHoriz_Adapter workAdapter;
    private Category category;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 15;
    private ShopViewModel shopViewModel;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_works);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_works), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shopViewModel = new ShopViewModel(new ShopUseCase(new ShopRepositoryImpl()));

        rvWorks = (RecyclerView) findViewById(R.id.rv_works);
        tvCategoryTag = (TextView) findViewById(R.id.tv_category_tag);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        btnNext =  (Button) findViewById(R.id.btn_next);

        workList = new ArrayList<>();
        workAdapter = new WorkHoriz_Adapter(workList,this);
        rvWorks.setLayoutManager(new LinearLayoutManager(this));
        rvWorks.setAdapter(workAdapter);

        category = getIntent().getParcelableExtra("category");
        if(category != null) {
            tvCategoryTag.setText(category.getTag());
            loadWorksByTag(category.getTag(),currentPage);
        } else {
            finish();
        }

        btnPrevious.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                loadWorksByTag(category.getTag(),currentPage);
            }
        });

        btnNext.setOnClickListener(v -> {
            if(currentPage < totalPages) {
                currentPage++;
                loadWorksByTag(category.getTag(),currentPage);
            }
        });
    }

    private void loadWorksByTag(String tag, int page) {
        shopViewModel.getWorksByTag(this, tag, page, ITEMS_PER_PAGE, new ShopViewModel.OnGetWorksByTagListener() {
            @Override
            public void onSuccess(WorkResponse data) {
                workList.clear();
                workList.addAll(data.getWorks());
                workAdapter.notifyDataSetChanged();

                totalPages = data.getTotal_pages();
                btnPrevious.setEnabled(currentPage > 1);
                btnNext.setEnabled(currentPage < totalPages);
                Log.d("CategoryWorksActivity", "Loaded " + data.getWorks().size() + " works for tag " + tag + ", page " + page + ", total pages: " + totalPages);
            }

            @Override
            public void onFailure(String error) {
                Log.e("CategoryWorksActivity", "Error loading works: " + error);
            }
        });
    }
}