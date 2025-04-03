package com.example.bookmoth.ui.activity.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookmoth.R;
import com.example.bookmoth.domain.model.shop.Category;
import com.example.bookmoth.domain.model.shop.Chapter;
import com.example.bookmoth.domain.model.shop.Profile;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;

import java.util.List;

public class WorkInforFragment extends Fragment {
    private static final String ARG_WORK_ID = "work_id";
    private TextView tvAuthor, tvCategories, tvChapterCount, tvDescription;
    private ShopViewModel shopViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work_infor,container,false);
        tvAuthor = view.findViewById(R.id.tv_author);
        tvCategories = view.findViewById(R.id.tv_categories);
        tvChapterCount = view.findViewById(R.id.tv_chapter_count);
        tvDescription = view.findViewById(R.id.tv_description);

        if(getArguments() != null) {
            int  workId = getArguments().getInt(ARG_WORK_ID);

            shopViewModel.getWorkById(getContext(), workId, new ShopViewModel.OnGetWorkByIdListener() {
                @Override
                public void onSuccess(Work work) {
                    tvDescription.setText("Giới thiệu: " + work.getDescription());

                    shopViewModel.getProfileById(getContext(), work.getWork_id(), new ShopViewModel.OnGetProfileByIdListener() {
                        @Override
                        public void onSuccess(Profile data) {
                            tvAuthor.setText("Tác giả: " + data.getUsername());
                        }

                        @Override
                        public void onFailure(String error) {
                            tvAuthor.setText("Tác giả: Unknown");
                        }
                    });
                }

                @Override
                public void onFailure(String error) {
                    tvDescription.setText("Giới thiệu: Không có thông tin");
                }
            });

            shopViewModel.getCategoriesByWorkId(getContext(), workId, new ShopViewModel.OnGetCategoriesByWorkIdListener() {
                @Override
                public void onSuccess(List<Category> data) {
                    StringBuilder categoryList = new StringBuilder("Thể loại: ");
                    for(Category category : data) {
                        categoryList.append(category.getTag()).append(", ");
                    }
                    tvCategories.setText(categoryList.toString().replace(", $", ""));
                }

                @Override
                public void onError(String error) {
                    tvCategories.setText("Thể loại: Không có thông tin");
                }
            });

            shopViewModel.getChaptersByWorkId(getContext(), workId, new ShopViewModel.OnGetChaptersByWorkIdListener() {
                @Override
                public void onSuccess(List<Chapter> data) {
                    tvChapterCount.setText("Số chương:  " + data.size());
                }

                @Override
                public void onError(String error) {
                    tvChapterCount.setText("Số chương: 0");
                }
            });

        }
        return view;
    }

    public static WorkInforFragment newInstance(int workId) {
        WorkInforFragment fragment = new WorkInforFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_WORK_ID, workId);
        fragment.setArguments(args);
        return fragment;
    }
}
