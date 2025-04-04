package com.example.bookmoth.ui.activity.authorcrud;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.core.libraryutils.InnerToast;
import com.example.bookmoth.ui.dialogs.BottomConfirmDialog;
import com.example.bookmoth.ui.viewmodel.authorcrud.AddWorkViewModel;

import java.util.Locale;

public class AddWorkActivity extends AppCompatActivity {
    private AddWorkViewModel viewModel;

    private ImageButton btn_back;
    private Button btn_submit;
    private Button btn_addcover;

    private TextView inp_title;
    private TextView inp_desc;
    private Uri inp_cover_uri;
    private TextView inp_price;
    private FrameLayout frame_loading;

    private ImageView img_coverpreview;

    private ActivityResultLauncher<Intent> pickImgArl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            res -> {
                if (res.getResultCode() == Activity.RESULT_OK && res.getData() != null) {
                    inp_cover_uri = res.getData().getData();
                    img_coverpreview.setImageURI(inp_cover_uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_work);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initObjects();
        initFunctions();
        initLiveData();
    }

    private void initObjects() {
        viewModel = new ViewModelProvider(this).get(AddWorkViewModel.class);

        btn_addcover = findViewById(R.id.addwork_btn_addcover);
        btn_submit = findViewById(R.id.addwork_btn_submit);
        btn_back = findViewById(R.id.imgbtn_back);

        img_coverpreview = findViewById(R.id.addwork_img_coverpreview);

        inp_title = findViewById(R.id.addwork_title);
        inp_desc = findViewById(R.id.addwork_description);
        inp_price = findViewById(R.id.addwork_price);

        frame_loading = findViewById(R.id.frame_loading);
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

    private void initLiveData() {
        viewModel.getMessage().observe(this, v -> {
            frame_loading.setVisibility(View.GONE);
            if (v.isEmpty()) {
                InnerToast.show(this, getString(R.string.addwork_res_success));
                finish();
            } else {
                ErrorDialog.showError(this, String.format(Locale.getDefault(), "%s:\n%s", getString(R.string.addwork_res_failed), v));
            }
        });
    }

    private boolean isFormInputLegit() {
        boolean res = true;

        if (inp_title.getText() == null || inp_title.getText().toString().isEmpty()) {
            inp_title.setError(getString(R.string.work_input_title_error_blank));
            res = false;
        } else inp_title.setError(null);
        if (inp_price.getText() == null || inp_title.getText().toString().isEmpty()) {
            inp_price.setError(getString(R.string.work_input_pricing_warning_defaultprice));
        } else inp_price.setError(null);

        return res;
    }

    private Bundle compileInfoBundle() {
        Bundle infos = new Bundle();
        infos.putString("title", inp_title.getText().toString());
        infos.putString("description", inp_desc.getText().toString());
        infos.putParcelable("cover_uri", inp_cover_uri);
        int price;
        try {
            price = Integer.parseInt(inp_price.getText().toString());
        } catch (Exception e) {
            price = 0;
        }
        infos.putInt("price", price);
        return infos;
    }

    private void finalInfoCheck(Bundle infos) {
        BottomConfirmDialog finalCheckDialog = new BottomConfirmDialog(this);
        finalCheckDialog.setTitle(getString(R.string.addwork_confirm_title));
        finalCheckDialog.setClarifyText(getString(R.string.addwork_confirm_clarify));
        View infosReviewView = LayoutInflater.from(this).inflate(R.layout.dialog_work_infosreview, null);
            ((ImageView)infosReviewView.findViewById(R.id.work_fin_img)).setImageURI(infos.getParcelable("cover_uri"));
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
                viewModel.setInfoBundle(infos, AddWorkActivity.this);
                finalCheckDialog.dismiss();
                frame_loading.setVisibility(View.VISIBLE);
                Glide.with(AddWorkActivity.this).load(R.drawable.animation_loading).into((ImageView)findViewById(R.id.frame_loading_gif));
            }
        });
        finalCheckDialog.show();
    }
}