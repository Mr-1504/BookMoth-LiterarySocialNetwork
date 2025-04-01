package com.example.bookmoth.ui.activity.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bookmoth.R;
import com.example.bookmoth.core.utils.Extension;
import com.example.bookmoth.core.utils.GenderUtils;
import com.example.bookmoth.data.model.profile.ProfileDatabase;
import com.example.bookmoth.data.local.utils.ImageCache;
import com.example.bookmoth.data.repository.profile.LocalProfileRepositoryImpl;
import com.example.bookmoth.data.repository.profile.ProfileRepositoryImpl;
import com.example.bookmoth.databinding.ActivityEditProfileBinding;
import com.example.bookmoth.core.enums.Gender;
import com.example.bookmoth.domain.model.profile.Profile;
import com.example.bookmoth.domain.usecase.profile.ProfileUseCase;
import com.example.bookmoth.ui.dialogs.LoadingUtils;
import com.example.bookmoth.ui.viewmodel.profile.ProfileViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {


    private ProfileViewModel profileViewModel;
    private TextView editAvatar, editCover, editUsername;
    private TextView editFullname, editGender, editBirthday;
    private ImageView avatar, cover, back;
    private TextView fullName, usename, gender, birth;
    private ActivityEditProfileBinding binding;
    private int requestCode;
    private boolean isChangeAvatar, isChangeCover, identifier;
    private static final int PICK_AVATAR_IMAGE_REQUEST = 1;
    private static final int PICK_COVER_IMAGE_REQUEST = 2;
    private static final int REQUEST_STORAGE_PERMISSION = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        clickEditAvatar();
        clickEditCover();
        clickEditUsername();
        clickEditFullname();
        clickEditBirth();
        clickEditGender();
        clickBack();
    }

    private void clickBack() {
        back.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.confirm));
            builder.setMessage(getString(R.string.do_you_want_to_save_change));
            builder.setPositiveButton("Lưu", null);
            builder.setNegativeButton("Hủy", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });

            AlertDialog dialog = builder.create();
            dialog.setOnShowListener(dialogInterface -> {
                Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                saveButton.setOnClickListener(v -> {
                    LoadingUtils.showLoading(getSupportFragmentManager());
                    String fullname = fullName.getText().toString();
                    String[] splitedName = fullname.split(" ");

                    StringBuilder lastname = new StringBuilder();
                    for (int i = 0; i < splitedName.length - 1; i++) {
                        lastname.append(splitedName[i]);
                        if (i < splitedName.length - 2) {
                            lastname.append(" ");
                        }
                    }

                    String firstname = splitedName[splitedName.length - 1];
                    String birth = this.birth.getText().toString();
                    String username = this.usename.getText().toString();


                    MultipartBody.Part avatar = null;
                    if (isChangeAvatar)
                        avatar =
                                Extension.prepareFilePartFromImageView(this.avatar, "avatar", this);

                    MultipartBody.Part cover = null;
                    if (isChangeCover)
                        cover =
                                Extension.prepareFilePartFromImageView(this.cover, "cover", this);

                    int gender = GenderUtils.getGenderIntFromString(this.gender.getText().toString());

                    profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
                        @Override
                        public void onProfileSuccess(Profile profile) {
                            identifier = profile.isIdentifier();
                        }

                        @Override
                        public void onProfileFailure(String error) {

                        }
                    });

                    Map<String, RequestBody> params = new HashMap<>();
                    params.put("firstName", RequestBody.create(firstname, MediaType.parse("text/plain")));
                    params.put("lastName", RequestBody.create(lastname.toString(), MediaType.parse("text/plain")));
                    params.put("username", RequestBody.create(username, MediaType.parse("text/plain")));
                    params.put("gender", RequestBody.create(String.valueOf(gender), MediaType.parse("text/plain")));
                    params.put("identifier", RequestBody.create(String.valueOf(identifier), MediaType.parse("text/plain")));
                    params.put("birth", RequestBody.create(birth, MediaType.parse("text/plain")));

                    profileViewModel.editProfile(this, params, avatar, cover, new ProfileViewModel.OnEditProfile() {
                                @Override
                                public void onSuccess(Profile profile) {
                                    LoadingUtils.hideLoading();
                                    dialog.dismiss();
                                    profileViewModel.saveProfile(profile);
                                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }

                                @Override
                                public void onError(String error) {
                                    LoadingUtils.hideLoading();
                                    dialog.dismiss();
                                    showErrorDialog(error);
                                }
                    });
                });
            });
            dialog.show();
        });
    }

    private void clickEditGender() {
        editGender.setOnClickListener(view -> showEditGenderDialog());
    }

    private void showEditGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Giới Tính");

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setPadding(10, 10, 10, 10);
        RadioButton maleButton = new RadioButton(this);
        maleButton.setText(getString(R.string.male));
        RadioButton femaleButton = new RadioButton(this);
        femaleButton.setText(getString(R.string.female));
        RadioButton otherButton = new RadioButton(this);
        otherButton.setText(getString(R.string.other));

        radioGroup.addView(maleButton);
        radioGroup.addView(femaleButton);
        radioGroup.addView(otherButton);

        builder.setView(radioGroup);
        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(v -> {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadio = dialog.findViewById(selectedId);
                    String gender = selectedRadio.getText().toString();
                    this.gender.setText(gender);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Vui lòng chọn giới tính!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }


    private void clickEditBirth() {
        editBirthday.setOnClickListener(view -> showEditBirthDateDialog());
    }

    private void showEditBirthDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Ngày Sinh");

        // Tạo EditText để nhập ngày
        EditText input = new EditText(this);
        input.setHint(this.birth.getText().toString() == "" ? "DD/MM/YYYY" : this.birth.getText().toString());
        input.setFocusable(false); // Không cho nhập tay, chỉ chọn từ DatePicker
        input.setInputType(InputType.TYPE_NULL);

        // Khi nhấn vào EditText -> Mở DatePickerDialog
        input.setOnClickListener(v -> showDatePickerDialog(input));

        builder.setView(input);
        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {

            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {
                String ngaySinh = input.getText().toString();
                String[] parts = ngaySinh.split(" ");
                if (parts.length == 4) {
                    int dayOfMonth = Integer.parseInt(parts[0]);

                    int _month = Integer.parseInt(parts[2].replace(",", ""));
                    int _year = Integer.parseInt(parts[3]);
                    String strDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String strMonth = _month < 9 ? "0" + _month : String.valueOf(_month);

                    ngaySinh = String.format("%s/%s/%s", strDay, strMonth, _year);
                }

                if (validateDateOfBirth(ngaySinh)) {
                    this.birth.setText(ngaySinh);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Ngày sinh không hợp lệ!", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    /**
     * Hiển thị DatePickerDialog để người dùng chọn ngày sinh.
     * Nếu ngày sinh đã có trong EditText, sử dụng ngày đó làm giá trị mặc định.
     */
    private void showDatePickerDialog(EditText edtDate) {
        Calendar calendar = Calendar.getInstance();

        String dateText = edtDate.getText().toString().trim();
        if (!dateText.isEmpty()) {
            try {
                String[] parts = dateText.split(" tháng |, ");
                int selectedDay = Integer.parseInt(parts[0]);
                int selectedMonth = Integer.parseInt(parts[1]) - 1;
                int selectedYear = Integer.parseInt(parts[2]);

                calendar.set(selectedYear, selectedMonth, selectedDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Cập nhật TextView với ngày đã chọn
                    String selectedDate = selectedDay + " tháng " + (selectedMonth + 1) + ", " + selectedYear;
                    edtDate.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
    }


    /**
     * Kiểm tra ngày sinh có hợp lệ hay không.
     * Điều kiện:
     * - Ngày sinh phải nhỏ hơn ngày hiện tại.
     * - Tuổi phải từ 12 trở lên.
     *
     * @param dateOfBirth Ngày sinh ở định dạng "dd/MM/yyyy".
     * @return true nếu ngày sinh hợp lệ, ngược lại trả về false.
     */
    private boolean validateDateOfBirth(String dateOfBirth) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        Calendar today = Calendar.getInstance();
        try {
            Date date = sdf.parse(dateOfBirth);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(date);

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            return !date.after(new Date()) && age >= 12;
        } catch (ParseException e) {
            return false;
        }
    }

    private void clickEditFullname() {
        editFullname.setOnClickListener(view -> showEditFullNameDialog());
    }

    private void showEditFullNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Họ và Tên");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        EditText edtFirstName = new EditText(this);
        edtFirstName.setHint("Tên");
        layout.addView(edtFirstName);

        EditText edtLastName = new EditText(this);
        edtLastName.setHint("Họ");
        layout.addView(edtLastName);

        builder.setView(layout);
        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {

            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {
                String firstname = edtFirstName.getText().toString();
                String lastname = edtLastName.getText().toString();
                if (firstname.isEmpty() || lastname.isEmpty()) {
                    Toast.makeText(this, "Không được để trống!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (firstname.length() < 2 || lastname.length() < 2) {
                    Toast.makeText(this, "Tên không hợp lệ!", Toast.LENGTH_SHORT).show();
                    return;
                }

                fullName.setText(lastname + " " + firstname);
                dialog.dismiss();
            });
        });

        dialog.show();
    }


    private void clickEditUsername() {
        editUsername.setOnClickListener(view -> showEditUsernameDialog());
    }

    private void showEditUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa Username");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine();
        input.requestFocus();

        builder.setView(input);
        builder.setPositiveButton("Lưu", null);
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {

            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            saveButton.setOnClickListener(v -> {
                String newUsername = input.getText().toString().trim();

                if (newUsername.isEmpty()) {
                    Toast.makeText(this, "Username không được để trống!", Toast.LENGTH_SHORT).show();
                    return; // Không đóng dialog
                }

                if (newUsername.length() < 7) {
                    Toast.makeText(this, "Username phải dài hơn 7 ký tự!", Toast.LENGTH_SHORT).show();
                    return; // Không đóng dialog
                }

                LoadingUtils.showLoading(getSupportFragmentManager());
                profileViewModel.checkUsername(this, newUsername, new ProfileViewModel.OnCheckUsernameListener() {
                    @Override
                    public void onProfileSuccess(boolean exists) {
                        LoadingUtils.hideLoading();
                        if (!exists) {
                            usename.setText(newUsername);
                            dialog.dismiss(); // Chỉ đóng khi username hợp lệ
                        } else {
                            Toast.makeText(EditProfileActivity.this, "Username đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        LoadingUtils.hideLoading();
                        Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        dialog.show();
    }


    private void clickEditCover() {
        editCover.setOnClickListener(view -> {
            requestCode = PICK_COVER_IMAGE_REQUEST;
            checkPermissionAndOpenGallery();
        });
    }

    private void clickEditAvatar() {
        editAvatar.setOnClickListener(view -> {
            requestCode = PICK_AVATAR_IMAGE_REQUEST;
            checkPermissionAndOpenGallery();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == this.requestCode && resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == PICK_AVATAR_IMAGE_REQUEST) {
                    isChangeAvatar = true;
                    avatar.setImageURI(data.getData());
                } else if (requestCode == PICK_COVER_IMAGE_REQUEST) {
                    isChangeCover = true;
                    cover.setImageURI(data.getData());
                }
            }
        }
    }

    private void init() {
        identifier = false;
        isChangeAvatar = false;
        isChangeCover = false;
        LocalProfileRepositoryImpl localRepo = new LocalProfileRepositoryImpl(
                this, ProfileDatabase.getInstance(this).profileDao()
        );
        profileViewModel = new ProfileViewModel(new ProfileUseCase(localRepo, new ProfileRepositoryImpl()));
        editAvatar = binding.editAvatar;
        editCover = binding.editCover;
        editUsername = binding.editUsername;
        editFullname = binding.editFullname;
        editGender = binding.editGender;
        editBirthday = binding.editBirth;
        avatar = binding.imgAvatar;
        cover = binding.imgCover;
        back = binding.btnBack;
        fullName = binding.txtFullname;
        usename = binding.txtUsername;
        gender = binding.txtGender;
        birth = binding.txtBirth;

        avatar.setImageBitmap(ImageCache.loadBitmap(this, "avatar.png"));
        cover.setImageBitmap(ImageCache.loadBitmap(this, "cover.png"));
        profileViewModel.isProfileExist(exist -> {
            if (exist) {
                profileViewModel.getProfileLocal(new ProfileViewModel.OnProfileListener() {
                    @Override
                    public void onProfileSuccess(Profile profile) {
                        setInformation(profile);
                    }

                    @Override
                    public void onProfileFailure(String error) {
                        getMe();
                    }
                });
            } else {
                getMe();
            }
        });
    }

    private void setInformation(Profile profile) {
        usename.setText(profile.getUsername());
        fullName.setText(String.format("%s %s", profile.getLastName(), profile.getFirstName()));
        birth.setText(profile.getBirth() == null ? "" : profile.getBirth());
        Gender gen = GenderUtils.getGenderFromInt(profile.getGender());
        gender.setText(GenderUtils.getGenderDisplayName(EditProfileActivity.this, gen));
    }

    private void getMe() {
        profileViewModel.getProfile(this, new ProfileViewModel.OnProfileListener() {
            @Override
            public void onProfileSuccess(Profile profile) {
                setInformation(profile);
            }

            @Override
            public void onProfileFailure(String error) {
                Toast.makeText(EditProfileActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndOpenGallery() {
        String permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "Bạn cần cấp quyền truy cập ảnh trong Cài đặt!", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_STORAGE_PERMISSION);
            }
        } else {
            openGallery();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
}