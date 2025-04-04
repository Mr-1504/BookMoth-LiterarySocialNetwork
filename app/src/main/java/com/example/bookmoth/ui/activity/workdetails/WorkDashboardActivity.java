package com.example.bookmoth.ui.activity.workdetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.DateTimeFormat;
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.core.libraryutils.InnerToast;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.adapter.WorkDashboardRecyclerViewAdapter;
import com.example.bookmoth.ui.activity.authorcrud.AddChapterActivity;
import com.example.bookmoth.ui.activity.authorcrud.UpdateChapterActivity;
import com.example.bookmoth.ui.activity.authorcrud.UpdateWorkActivity;
import com.example.bookmoth.ui.activity.reader.ReadingActivity;
import com.example.bookmoth.ui.dialogs.BottomConfirmDialog;
import com.example.bookmoth.ui.viewmodel.workview.WorkDashboardViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class WorkDashboardActivity extends AppCompatActivity {
    private WorkDashboardViewModel viewModel;

    private RecyclerView rv;
    private WorkDashboardRecyclerViewAdapter rv_adapter;

    private int work_id;
    private Work work = new Work();
    private List<Chapter> chapters = new ArrayList<>();
    private ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_work_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        work_id = getIntent().getIntExtra("work_id", 0);

        initObjects();
        initFunctions();
        initLiveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.fetchWork(work_id);
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(this).get(WorkDashboardViewModel.class);

        rv = findViewById(R.id.workdash_rv_details);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv_adapter = new WorkDashboardRecyclerViewAdapter(work, chapters);
        rv.setAdapter(rv_adapter);

        btn_back = findViewById(R.id.imgbtn_back);
    }

    private void initFunctions() {
        btn_back.setOnClickListener(v -> finish());

        rv_adapter.attachQuickActionListener(
            wid -> {
                Intent it = new Intent(this, AddChapterActivity.class);
                Bundle req = AddChapterActivity.makeRequirementBundle(LibraryConst.TEST_TOKEN, new ArrayList<Work>(Arrays.asList(work)));
                it.putExtra("requirement", req);
                startActivity(it);
            },
            wid -> {
                BottomConfirmDialog cf = new BottomConfirmDialog(this);
                cf.setTitle(getString(R.string.remwork_confirm_title));
                cf.setClarifyText(getString(R.string.remwork_confirm_clarify));
                cf.setFinalActionNote(getString(R.string.general_areyousure));
                cf.setSubmitClickNeeded(3);
                cf.setOnMadeDecisionListener(new BottomConfirmDialog.OnMadeDecisionListener() {
                    @Override
                    public void cancel() {
                        cf.dismiss();
                    }

                    @Override
                    public void submit() {
                        cf.dismiss();
                        findViewById(R.id.frame_loading).setVisibility(View.VISIBLE);
                        Glide.with(WorkDashboardActivity.this).load(R.drawable.animation_loading).into((ImageView)findViewById(R.id.frame_loading_gif));
                        viewModel.removeWork(wid);
                    }
                });
                cf.show();
            },
            wid -> {
                Intent it = new Intent(this, UpdateWorkActivity.class);
                it.putExtra("work", work);
                startActivity(it);
            });
        rv_adapter.attachChapterClickListener(pos -> {
            BottomSheetDialog chapterActionDialog = new BottomSheetDialog(this);
            View chapterQAView = LayoutInflater.from(this).inflate(R.layout.item_workdash_chapterquickaction, null);
            ((TextView)chapterQAView.findViewById(R.id.wdchapter_tv_toolbartitle)).setText(String.format(Locale.getDefault(), "%s %d", getString(R.string.chapter_chapter), pos+1));
            chapterQAView.findViewById(R.id.wdchapter_fl_readchapter).setOnClickListener(v -> {
                chapterActionDialog.dismiss();
                Intent it = new Intent(this, ReadingActivity.class);
                Bundle req = ReadingActivity.makeRequirementBundle(chapters, work.getTitle(), pos);
                it.putExtra("requirement", req);
                startActivity(it);
            });
            chapterQAView.findViewById(R.id.wdchapter_fl_deletechapter).setOnClickListener(v -> {
                chapterActionDialog.dismiss();
                BottomConfirmDialog cf = new BottomConfirmDialog(this);
                cf.setTitle(getString(R.string.remchap_confirm_title));
                cf.setClarifyText(getString(R.string.remchap_confirm_clarify));
                cf.setFinalActionNote(getString(R.string.general_areyousure));
                cf.setSubmitClickNeeded(3);
                cf.setOnMadeDecisionListener(new BottomConfirmDialog.OnMadeDecisionListener() {
                    @Override
                    public void cancel() {
                        cf.dismiss();
                    }

                    @Override
                    public void submit() {
                        cf.dismiss();
                        findViewById(R.id.frame_loading).setVisibility(View.VISIBLE);
                        Glide.with(WorkDashboardActivity.this).load(R.drawable.animation_loading).into((ImageView) findViewById(R.id.frame_loading_gif));
                        viewModel.removeChaper(chapters.get(pos).getChapter_id());
                    }
                });
                cf.show();
            });
            chapterQAView.findViewById(R.id.wdchapter_fl_editchapter).setOnClickListener(v -> {
                chapterActionDialog.dismiss();
                Intent it = new Intent(this, UpdateChapterActivity.class);
                it.putExtra("chapter", chapters.get(pos));
                startActivity(it);
            });
            chapterActionDialog.setContentView(chapterQAView);
            chapterActionDialog.show();
        });
    }

    private void initLiveData() {
        viewModel.getWorkStats().observe(this, v -> rv.post(() -> {
            ((TextView)findViewById(R.id.workdash_tv_view)).setText(String.valueOf(v.getView_count()));
            ((TextView)findViewById(R.id.workdash_tv_follow)).setText(String.valueOf(v.getFollow_count()));
            ((TextView)findViewById(R.id.workdash_tv_lastupdate)).setText(DateTimeFormat.format(v.getLast_update(), DateTimeFormat.DATE_ONLY));
            ((TextView)findViewById(R.id.workdash_tv_price)).setText(String.valueOf(v.getPrice()));
        }));
        viewModel.getChapters().observe(this, v -> {
            chapters.clear();
            chapters.addAll(v);
        });
        viewModel.getWork().observe(this, v -> {
            Work.cloneValue(work, v);
            rv_adapter.notifyDataSetChanged();
        });
        viewModel.getMessage().observe(this, v -> {
            findViewById(R.id.frame_loading).setVisibility(View.GONE);
            String successString = "";
            String failedString = "";
            if (viewModel.getAction() == WorkDashboardViewModel.ACTION_REMWORK) {
                successString = getString(R.string.remwork_res_success);
                failedString = getString(R.string.remwork_res_failed);
            }
            else if (viewModel.getAction() == WorkDashboardViewModel.ACTION_REMCHAP) {
                successString = getString(R.string.remchap_res_success);
                failedString = getString(R.string.remchap_res_failed);
            }
            if (v.isEmpty()) {
                InnerToast.show(this, successString);
                finish();
            } else {
                ErrorDialog.showError(this, String.format(Locale.getDefault(), "%s:\n%s", failedString, v));
            }
        });
    }
}