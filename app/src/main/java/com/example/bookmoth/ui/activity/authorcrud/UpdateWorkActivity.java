package com.example.bookmoth.ui.activity.authorcrud;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.ui.dialogs.BottomConfirmDialog;
import com.example.bookmoth.ui.viewmodel.authorcrud.UpdateWorkViewModel;

import java.util.Locale;

public class UpdateWorkActivity extends AppCompatActivity {
    private UpdateWorkViewModel viewModel;

    private TextView title;
    private TextView desc;
    private Uri cover_uri;
    private TextView price;

    private Button btn_addcover;
    private ImageView img_coverpreview;

    private Button btn_submit;
    private ImageButton btn_back;

    private Work work;

    private ActivityResultLauncher<Intent> pickImgArl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() == Activity.RESULT_OK && res.getData() != null) {
                    cover_uri = res.getData().getData();
                    img_coverpreview.setImageURI(cover_uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_work);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        work = (Work) getIntent().getSerializableExtra("work");

        initObjects();
        initFunctions();
        initGraphics();
        initLiveData();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(this).get(UpdateWorkViewModel.class);
        title = findViewById(R.id.updatework_title);
        desc = findViewById(R.id.updatework_description);
        price = findViewById(R.id.updatework_price);
        btn_addcover = findViewById(R.id.updatework_btn_addcover);
        img_coverpreview = findViewById(R.id.updatework_img_coverpreview);
        btn_submit = findViewById(R.id.updatework_btn_submit);
        btn_back = findViewById(R.id.imgbtn_back);
    }

    private void initFunctions() {
        btn_back.setOnClickListener(v -> finish());
        btn_addcover.setOnClickListener(v -> {
            Intent pickImg = new Intent(Intent.ACTION_PICK);
            pickImg.setType("image/*");
            pickImgArl.launch(pickImg);
        });
        btn_submit.setOnClickListener(v -> {
            if (isFormInputLegit()) finalInfoCheck(compileInfoBundle());
        });
    }

    private void initGraphics() {
        title.setText(work.getTitle());
        desc.setText(work.getDescription());
        price.setText(String.format("%.2f", work.getPrice()));
        if (work.getCover_url() != null)
            Glide.with(this)
                    .load(AppConst.API_URL + AppConst.CDN_COVER + work.getCover_url() + "?v=" + System.currentTimeMillis())
                    .into(img_coverpreview);
    }

    private void initLiveData() {
        viewModel.getMessage().observe(this, v -> {
            findViewById(R.id.frame_loading).setVisibility(View.GONE);
            if (v.isEmpty()) {
                InnerToast.show(this, getString(R.string.updatework_res_success));
                finish();
            } else {
                ErrorDialog.showError(this, String.format(Locale.getDefault(), "%s:\n%s", getString(R.string.updatework_res_failed), v));
            }
        });
    }

    private boolean isFormInputLegit() {
        boolean res = true;

        if (title.getText() == null || title.getText().toString().isEmpty()) {
            title.setError(getString(R.string.work_input_title_error_blank));
            res = false;
        } else title.setError(null);
        if (price.getText() == null || price.getText().toString().isEmpty()) {
            price.setError(getString(R.string.work_input_pricing_warning_defaultprice));
        } else price.setError(null);

        return res;
    }

    private Bundle compileInfoBundle() {
        Bundle infos = new Bundle();
        infos.putInt("work_id", work.getWork_id());
        infos.putString("title", title.getText().toString());
        infos.putString("description", desc.getText().toString());
        infos.putParcelable("cover_uri", cover_uri);
        int priceInfo;
        try {
            priceInfo = Integer.parseInt(price.getText().toString());
        } catch (Exception e) {
            priceInfo = 0;
        }
        infos.putInt("price", priceInfo);
        return infos;
    }

    private void finalInfoCheck(Bundle infos) {
        BottomConfirmDialog finalCheckDialog = new BottomConfirmDialog(this);
        finalCheckDialog.setTitle(getString(R.string.addwork_confirm_title));
        finalCheckDialog.setClarifyText(getString(R.string.updatework_confirm_clarify));
        View infosReviewView = LayoutInflater.from(this).inflate(R.layout.dialog_work_infosreview, null);
        if (infos.getParcelable("cover_uri") != null)
            ((ImageView)infosReviewView.findViewById(R.id.work_fin_img)).setImageURI(infos.getParcelable("cover_uri"));
        else
            ((ImageView)infosReviewView.findViewById(R.id.work_fin_img)).setImageDrawable(img_coverpreview.getDrawable());
        ((TextView)infosReviewView.findViewById(R.id.work_fin_title)).setText(infos.getString("title"));
        ((TextView)infosReviewView.findViewById(R.id.work_fin_price)).setText(String.format(Locale.getDefault(), "%s: %d", getString(R.string.general_price), infos.getInt("price")));
        ((TextView)infosReviewView.findViewById(R.id.work_fin_desc)).setText(infos.getString("description"));
        finalCheckDialog.setClarifyView(infosReviewView);
        finalCheckDialog.setOnMadeDecisionListener(new BottomConfirmDialog.OnMadeDecisionListener() {
            @Override
            public void cancel() {
                finalCheckDialog.dismiss();
            }

            @Override
            public void submit() {
                viewModel.doUpdateWork(infos, UpdateWorkActivity.this);
                finalCheckDialog.dismiss();
                findViewById(R.id.frame_loading).setVisibility(View.VISIBLE);
                Glide.with(UpdateWorkActivity.this).load(R.drawable.animation_loading).into((ImageView)findViewById(R.id.frame_loading_gif));
            }
        });
        finalCheckDialog.show();
    }
}