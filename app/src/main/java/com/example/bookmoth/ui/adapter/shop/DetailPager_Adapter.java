package com.example.bookmoth.ui.adapter.shop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bookmoth.ui.activity.shop.WorkChaptersFragment;
import com.example.bookmoth.ui.activity.shop.WorkInforFragment;

public class DetailPager_Adapter extends FragmentStateAdapter {
    private final int workId;

    public DetailPager_Adapter(@NonNull FragmentActivity fragmentActivity, int workId) {
        super(fragmentActivity);
        this.workId = workId;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return WorkInforFragment.newInstance(workId);
        } else {
            return WorkChaptersFragment.newInstance(workId);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 2 tabs: "Thông tin" và "Chương"
    }
}
