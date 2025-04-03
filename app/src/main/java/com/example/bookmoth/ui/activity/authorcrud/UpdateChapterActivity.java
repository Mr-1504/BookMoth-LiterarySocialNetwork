package com.example.bookmoth.ui.activity.authorcrud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.bookmoth.core.libraryutils.AppConst;
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.core.libraryutils.InnerToast;
import com.example.bookmoth.core.libraryutils.ValueGen;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.ui.dialogs.BottomConfirmDialog;
import com.example.bookmoth.ui.viewmodel.authorcrud.UpdateChapterViewModel;

import java.util.Locale;

public class UpdateChapterActivity extends AppCompatActivity {
    private UpdateChapterViewModel viewModel;

    private Chapter chapter;

    private TextView inp_title;
    private TextView tv_filename;
    private Uri inp_content_uri;

    private Button btn_addcontent;
    private Button btn_submit;
    private ImageButton btn_back;

    private ActivityResultLauncher<Intent> pickContentFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() == AddChapterActivity.RESULT_OK && res.getData() != null) {
                    inp_content_uri = res.getData().getData();
                    tv_filename.setText(ValueGen.getFileNameFromUri(this, inp_content_uri));
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_chapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chapter = (Chapter) getIntent().getSerializableExtra("chapter");

        initObjects();
        initFunctions();
        initGraphics();
        initLiveData();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(this).get(UpdateChapterViewModel.class);

        inp_title = findViewById(R.id.updatechapter_title);
        tv_filename = findViewById(R.id.updatechapter_tv_contentfilename);

        btn_addcontent = findViewById(R.id.updatechapter_btn_addcontent);
        btn_submit = findViewById(R.id.updatechapter_btn_submit);
        btn_back = findViewById(R.id.imgbtn_back);
    }

    private void initFunctions() {
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
            BottomConfirmDialog caution = new BottomConfirmDialog(this);
            caution.setTitle(getString(R.string.updatechapter_confirm_title));
            caution.setClarifyText(getString(R.string.updatechapter_confirm_clarify));
            caution.setFinalActionNote(getString(R.string.general_areyousure));
            caution.setOnMadeDecisionListener(new BottomConfirmDialog.OnMadeDecisionListener() {
                @Override
                public void cancel() {
                    caution.dismiss();
                }

                @Override
                public void submit() {
                    caution.dismiss();
                    viewModel.doUpdateChapter(compileInfoBundle(), UpdateChapterActivity.this);
                    findViewById(R.id.frame_loading).setVisibility(View.VISIBLE);
                    Glide.with(UpdateChapterActivity.this).load(R.drawable.animation_loading).into((ImageView)findViewById(R.id.frame_loading_gif));
                }
            });
            caution.show();
        });
    }

    private void initGraphics() {
        inp_title.setText(chapter.getTitle());
    }

    private void initLiveData() {
        viewModel.getMessage().observe(this, v -> {
            findViewById(R.id.frame_loading).setVisibility(View.GONE);
            if (v.isEmpty()) {
                InnerToast.show(this, getString(R.string.updatechapter_res_success));
                finish();
            } else {
                ErrorDialog.showError(this, String.format(Locale.getDefault(), "%s:\n%s", getString(R.string.updatechapter_res_failed), v));
            }
        });
    }

    private Bundle compileInfoBundle() {
        Bundle infos = new Bundle();
        infos.putString("credential", AppConst.TEST_TOKEN);
        infos.putInt("chapter_id", chapter.getChapter_id());
        infos.putString("title", inp_title.getText().toString().isBlank() ? null : inp_title.getText().toString());
        if (inp_content_uri != null) {
            infos.putParcelable("content_uri", inp_content_uri);
            infos.putString("filename", tv_filename.getText().toString());
        }
        return infos;
    }
}