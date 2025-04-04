package com.example.bookmoth.ui.activity.authorcrud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.DateTimeFormat;
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.core.libraryutils.InnerToast;
import com.example.bookmoth.core.libraryutils.ValueGen;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.viewmodel.authorcrud.AddChapterViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddChapterActivity extends AppCompatActivity {
    private AddChapterViewModel viewModel;

    private AutoCompleteTextView inp_forwork;
    private TextView inp_title;

    private View ly_chapterinfo;
    private TextView tv_filename;

    private Button btn_addcontent;
    private ImageButton btn_back;
    private Button btn_submit;
    private FrameLayout frame_loading;

    private List<Work> works = new ArrayList<>();;
    private List<String> dropdownString;
    private Uri inp_content_uri;

    private ActivityResultLauncher<Intent> pickContentFile = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        res -> {
            if (res.getResultCode() == AddChapterActivity.RESULT_OK && res.getData() != null) {
                inp_content_uri = res.getData().getData();
                tv_filename.setText(ValueGen.getFileNameFromUri(this, inp_content_uri));
            }
        }
    );

    /**
     * Tạo Bundle với values cần thiết cho hoạt động của AddChapterActivity
     * @param works Danh sách tác phẩm
     * @return Bundle, key="requirement"
     */
    public static Bundle makeRequirementBundle(List<Work> works) {
        Bundle req = new Bundle();
        req.putSerializable("works", (Serializable) works);
        return req;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_chapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent().getBundleExtra("requirement") != null) {
            Bundle req = getIntent().getBundleExtra("requirement");
            works = (List<Work>) req.getSerializable("works");
        }

        initObjects();
        initFunctions();
        initGraphics();
        initLiveData();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(this).get(AddChapterViewModel.class);

        inp_forwork = findViewById(R.id.addchapter_forwork);
        dropdownString = new ArrayList<>();
        for (Work w : works) dropdownString.add(String.format(Locale.getDefault(), "%s - %s", DateTimeFormat.format(w.getPost_date(), DateTimeFormat.DATE_ONLY), w.getTitle()));
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdownString);
        inp_forwork.setAdapter(dropdownAdapter);

        inp_title = findViewById(R.id.addchapter_title);

        ly_chapterinfo = findViewById(R.id.addchapter_ll_chapterinfos);
        tv_filename = findViewById(R.id.addchapter_tv_contentfilename);

        btn_addcontent = findViewById(R.id.addchapter_btn_addcontent);
        btn_back = findViewById(R.id.imgbtn_back);
        btn_submit = findViewById(R.id.addchapter_btn_submit);

        frame_loading = findViewById(R.id.frame_loading);
    }

    private void initFunctions() {
        inp_forwork.setOnClickListener(v -> inp_forwork.showDropDown());
        inp_forwork.setOnItemClickListener((adapterView, view, i, l) -> ly_chapterinfo.setVisibility(View.VISIBLE));
        btn_back.setOnClickListener(v -> finish());
        btn_addcontent.setOnClickListener(v -> {
            Intent pickFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickFile.setType("*/*");
            pickFile.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                    "text/plain", // .txt
                    "text/markdown" // .md
            });
            pickFile.addCategory(Intent.CATEGORY_OPENABLE);
            pickContentFile.launch(pickFile);
        });
        btn_submit.setOnClickListener(v -> {
            if (isFormInputLegit()) {
                viewModel.setInfoBundle(compileInfoBundle(), this);
                frame_loading.setVisibility(View.VISIBLE);
                Glide.with(this).load(R.drawable.animation_loading).into((ImageView)findViewById(R.id.frame_loading_gif));
            }
        });
    }

    private void initGraphics() {
        ly_chapterinfo.setVisibility(View.INVISIBLE);
        if (dropdownString.size() == 1) {
            inp_forwork.setText(dropdownString.get(0), false);
            ly_chapterinfo.setVisibility(View.VISIBLE);
        }
    }

    private void initLiveData() {
        viewModel.getMessage().observe(this, v -> {
            frame_loading.setVisibility(View.GONE);
            if (v.isEmpty()) {
                InnerToast.show(this, getString(R.string.addchapter_res_success));
                finish();
            } else {
                ErrorDialog.showError(this, String.format(Locale.getDefault(), "%s:\n%s", getString(R.string.addchapter_res_failed), v));
            }
        });
    }

    private Bundle compileInfoBundle() {
        Bundle infos = new Bundle();
        infos.putInt("work_id", works.get(dropdownString.indexOf(inp_forwork.getText().toString())).getWork_id());
        infos.putString("title", inp_title.getText().toString().isBlank() ? null : inp_title.getText().toString());
        infos.putParcelable("content_uri", inp_content_uri);
        infos.putString("filename", tv_filename.getText().toString());
        return infos;
    }

    private boolean isFormInputLegit() {
        boolean res = true;

        if (inp_forwork.getText() == null || inp_forwork.getText().toString().isEmpty() || !dropdownString.contains(inp_forwork.getText().toString())) {
            inp_forwork.setError(getString(R.string.addchapter_input_forwork_error_notfound));
            res = false;
        } else inp_forwork.setError(null);
        if (inp_content_uri == null) {
            tv_filename.setText(getString(R.string.addchapter_input_addcontent_error_notfound));
            res = false;
        }

        return res;
    }
}