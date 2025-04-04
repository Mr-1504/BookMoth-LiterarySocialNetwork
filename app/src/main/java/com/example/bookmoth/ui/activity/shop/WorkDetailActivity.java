package com.example.bookmoth.ui.activity.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.adapter.shop.DetailPager_Adapter;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.math.BigDecimal;
import java.util.List;

public class WorkDetailActivity extends AppCompatActivity {
    private ShapeableImageView ivWorkCover;
    private TextView tvWorkTitle, tvAuthor, tvViewCount, tvChapterCount, tvPrice;
    private Button btnBuy;
    private TabLayout tabLayoutDetail;
    private ViewPager2 viewPagerDetail;
    private ShopViewModel shopViewModel;
    private Work work;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.work_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initWidget();

        shopViewModel = new ShopViewModel(new ShopUseCase(new ShopRepositoryImpl()));
        work = getIntent().getParcelableExtra("work");
        if (work != null) {
            String coverUrl = work.getCover_url();
            if (coverUrl != null && !coverUrl.isEmpty()) {
                Glide.with(this)
                        .load(coverUrl)
                        .into(ivWorkCover);
            }
            tvWorkTitle.setText(work.getTitle());
            tvViewCount.setText(String.valueOf(work.getView_count()));
            tvPrice.setText("Giá: " + formatPrice(work.getPrice()));

            shopViewModel.getProfileById(this, work.getWork_id(), new ShopViewModel.OnGetProfileByIdListener() {
                @Override
                public void onSuccess(Profile profile) {
                    tvAuthor.setText(profile.getUsername());
                }

                @Override
                public void onFailure(String error) {
                    tvAuthor.setText("Unknown Author");
                }
            });

            shopViewModel.getChaptersByWorkId(this, work.getWork_id(), new ShopViewModel.OnGetChaptersByWorkIdListener() {
                @Override
                public void onSuccess(List<Chapter> responses) {
                    tvChapterCount.setText(String.valueOf(responses.size()));
                }

                @Override
                public void onError(String error) {
                    tvChapterCount.setText("0");
                }
            });

            DetailPager_Adapter pagerAdapter = new DetailPager_Adapter(this, work.getWork_id());
            viewPagerDetail.setAdapter(pagerAdapter);

            new TabLayoutMediator(tabLayoutDetail, viewPagerDetail, (tab, position) -> {
                if (position == 0) {
                    tab.setText("Thông tin");
                } else {
                    tab.setText("Chương");
                }
            }).attach();
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkDetailActivity.this, ShopActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initWidget() {
        ivWorkCover = findViewById(R.id.iv_work_cover);
        tvWorkTitle = findViewById(R.id.tv_work_title);
        tvAuthor = findViewById(R.id.tv_author);
        tvViewCount = findViewById(R.id.tv_view_count);
        tvChapterCount = findViewById(R.id.tv_chapter_count);
        btnBuy = findViewById(R.id.btn_buy);
        tvPrice = findViewById(R.id.tv_price);
        tabLayoutDetail = findViewById(R.id.tab_layout_detail);
        viewPagerDetail = findViewById(R.id.view_pager_detail);
        btnBack = findViewById(R.id.btn_back);
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) return "0";
        return price.stripTrailingZeros().toPlainString();
    }
}