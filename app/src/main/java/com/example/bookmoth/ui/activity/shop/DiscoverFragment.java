package com.example.bookmoth.ui.activity.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bookmoth.R;
import com.example.bookmoth.data.repository.shop.ShopRepositoryImpl;
import com.example.bookmoth.domain.model.shop.Work;
import com.example.bookmoth.domain.usecase.shop.ShopUseCase;
import com.example.bookmoth.ui.adapter.shop.Work_Adapter;
import com.example.bookmoth.ui.viewmodel.shop.ShopViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private RecyclerView rvNewReleases, rvPopular;
    private List<Work> newReleases = new ArrayList<>();
    private List<Work> popular = new ArrayList<>();
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnableNewReleases, autoScrollRunnablePopular;
    private static final long AUTO_SCROLL_INTERVAL = 3000;
    private boolean scrollDirectionForward = true;
    private ShopViewModel shopViewModel;
    private boolean isUserScrolling = false;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        Log.d("HomeFragment", "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        rvNewReleases = view.findViewById(R.id.rv_new_releases);
        rvPopular = view.findViewById(R.id.rv_popular);
        if (rvNewReleases == null || rvPopular == null) {
            Log.e("HomeFragment", "RecyclerView is null");
            Toast.makeText(getContext(), "Lỗi: RecyclerView không tìm thấy", Toast.LENGTH_LONG).show();
            return view;
        }

        shopViewModel = new ShopViewModel(new ShopUseCase(new ShopRepositoryImpl()));
        Log.d("HomeFragment", "WorkRepository initialized");

        rvNewReleases.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        rvPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        rvNewReleases.setHasFixedSize(true);
        rvNewReleases.setItemViewCacheSize(20);
        rvNewReleases.setDrawingCacheEnabled(true);
        rvNewReleases.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        rvNewReleases.setNestedScrollingEnabled(false);

        rvPopular.setHasFixedSize(true);
        rvPopular.setItemViewCacheSize(20);
        rvPopular.setDrawingCacheEnabled(true);
        rvPopular.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Log.d("HomeFragment", "RecyclerView LayoutManagers set");
        rvPopular.setNestedScrollingEnabled(false);

        Work_Adapter newReleaseAdapter = new Work_Adapter(newReleases,getContext());

        Work_Adapter popularAdapter = new Work_Adapter(popular,getContext());

        rvNewReleases.setAdapter(newReleaseAdapter);
        rvPopular.setAdapter(popularAdapter);
        Log.d("HomeFragment", "RecyclerView Adapters set");
        final ViewPager2 viewPager = getActivity() != null ? getActivity().findViewById(R.id.view_pager) : null;
        if (viewPager == null) {
            Log.e("HomeFragment", "ViewPager2 not found in Activity");
        } else {
            rvNewReleases.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isUserScrolling = true;
                        viewPager.setUserInputEnabled(false);
                        Log.d("HomeFragment", "Disabled ViewPager2 scrolling while touching rvNewReleases");
                        stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        viewPager.setUserInputEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isUserScrolling = false;
                        viewPager.setUserInputEnabled(true);
                        Log.d("HomeFragment", "Enabled ViewPager2 scrolling after releasing rvNewReleases");
                        startAutoScroll();
                        break;
                }
                return false;
            });

            rvPopular.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isUserScrolling = true;
                        viewPager.setUserInputEnabled(false);
                        Log.d("HomeFragment", "Disabled ViewPager2 scrolling while touching rvPopular");
                        stopAutoScroll();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        viewPager.setUserInputEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
//                        isUserScrolling = false;
                        viewPager.setUserInputEnabled(true);
                        Log.d("HomeFragment", "Enabled ViewPager2 scrolling after releasing rvPopular");
                        startAutoScroll();
                        break;
                }
                return false;
            });
            rvNewReleases.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewPager.setUserInputEnabled(true);
                        Log.d("HomeFragment", "rvNewReleases stopped scrolling, enabled ViewPager2");
                        if (!isUserScrolling) {
                            startAutoScroll(); // Chỉ khởi động lại nếu không phải người dùng cuộn
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserScrolling = true;
                        viewPager.setUserInputEnabled(false);
                        Log.d("HomeFragment", "rvNewReleases scrolling by user, disabled ViewPager2");
                        stopAutoScroll();
                    }
                }
            });

            rvPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewPager.setUserInputEnabled(true);
                        Log.d("HomeFragment", "rvPopular stopped scrolling, enabled ViewPager2");
                        if (!isUserScrolling) {
                            startAutoScroll();
                        }
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isUserScrolling = true;
                        viewPager.setUserInputEnabled(false);
                        Log.d("HomeFragment", "rvPopular scrolling by user, disabled ViewPager2");
                        stopAutoScroll();
                    }
                }
            });
        }

        autoScrollHandler = new Handler(Looper.getMainLooper());
        autoScrollRunnableNewReleases = new Runnable() {
            @Override
            public void run() {
                if (newReleases.isEmpty() || isUserScrolling) return;
                LinearLayoutManager layoutManager = (LinearLayoutManager) rvNewReleases.getLayoutManager();
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = newReleases.size();

                if (scrollDirectionForward) {
                    if (lastVisiblePosition >= totalItemCount - 1) {
                        rvNewReleases.smoothScrollToPosition(0);
                        scrollDirectionForward = false;
                    } else {
                        rvNewReleases.smoothScrollToPosition(firstVisiblePosition + 1);
                    }
                } else {
                    if (firstVisiblePosition <= 0) {
                        rvNewReleases.smoothScrollToPosition(totalItemCount - 1);
                        scrollDirectionForward = true;
                    } else {
                        rvNewReleases.smoothScrollToPosition(firstVisiblePosition - 1);
                    }
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL);
            }
        };
        autoScrollRunnablePopular = new Runnable() {
            @Override
            public void run() {
                if (popular.isEmpty() || isUserScrolling) return;
                LinearLayoutManager layoutManager = (LinearLayoutManager) rvPopular.getLayoutManager();
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = popular.size();

                if (scrollDirectionForward) {
                    if (lastVisiblePosition >= totalItemCount - 1) {
                        rvPopular.smoothScrollToPosition(0);
                        scrollDirectionForward = false;
                    } else {
                        rvPopular.smoothScrollToPosition(firstVisiblePosition + 1);
                    }
                } else {
                    if (firstVisiblePosition <= 0) {
                        rvPopular.smoothScrollToPosition(totalItemCount - 1);
                        scrollDirectionForward = true;
                    } else {
                        rvPopular.smoothScrollToPosition(firstVisiblePosition - 1);
                    }
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_INTERVAL);
            }
        };
        fetchData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchData();
    }

    private void fetchData() {
        Log.d("HomeFragment", "Fetching data...");
        stopAutoScroll();

        shopViewModel.getNewReleases(getContext(), new ShopViewModel.OnGetNewReleasesListener() {
            @Override
            public void onSuccess(List<Work> data) {
                if (isAdded()) {
                    Log.d("HomeFragment", "New releases data received: " + data.size() + " items");
                    List<Work> newWorks = new ArrayList<>(data);
                    newReleases.clear();
                    newReleases.addAll(newWorks);
                    rvNewReleases.getAdapter().notifyDataSetChanged();
                    if (newWorks.isEmpty()) {
                        Log.w("HomeFragment", "New releases list is empty");
                        Toast.makeText(getContext(), "Không có truyện mới!", Toast.LENGTH_SHORT).show();
                    } else {
                        rvNewReleases.scrollToPosition(0);
                        startAutoScroll();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error fetching new releases: " + error);
            }
        });

        shopViewModel.getPopular(getContext(), new ShopViewModel.OnGetPopularListener() {
            @Override
            public void onSuccess(List<Work> work) {
                if (isAdded()) {
                    Log.d("HomeFragment", "Popular data received: " + work.size() + " items");
                    List<Work> newWorks = new ArrayList<>(work);
                    popular.clear();
                    popular.addAll(newWorks);
                    rvPopular.getAdapter().notifyDataSetChanged();
                    if (newWorks.isEmpty()) {
                        Log.w("HomeFragment", "Popular list is empty");
                        Toast.makeText(getContext(), "Không có truyện nổi bật!", Toast.LENGTH_SHORT).show();
                    } else {
                        rvPopular.scrollToPosition(0);
                        startAutoScroll();
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Log.e("HomeFragment", "Error fetching popular: " + error);
            }
        });
    }
    private void startAutoScroll() {
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnableNewReleases);
            autoScrollHandler.removeCallbacks(autoScrollRunnablePopular);
            autoScrollHandler.postDelayed(autoScrollRunnableNewReleases, AUTO_SCROLL_INTERVAL);
            autoScrollHandler.postDelayed(autoScrollRunnablePopular, AUTO_SCROLL_INTERVAL);
            Log.d("HomeFragment", "Auto-scroll started");
        }
    }
    private void stopAutoScroll() {
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnableNewReleases);
            autoScrollHandler.removeCallbacks(autoScrollRunnablePopular);
            Log.d("HomeFragment", "Auto-scroll stopped");
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!newReleases.isEmpty() && !popular.isEmpty()) {
            startAutoScroll();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoScroll();
        autoScrollHandler = null;
    }
}