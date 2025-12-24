package com.dinerestaurant.app.ui.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.User;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.UserApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetupActivity extends AppCompatActivity {

    // ===== Views =====
    EditText edtPhone, edtEmail, edtFullName, edtLocation;
    TextView edtDob, edtGender;
    Button btnContinue;
    ImageView btnBack, imgAvatar, btnEditAvatar;

    // ===== Data =====
    UserApi userApi;
    Uri selectedAvatarUri;

    // ===== Image Picker =====
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedAvatarUri = uri;

                            Glide.with(this)
                                    .load(uri)
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .error(R.drawable.ic_profile_placeholder)
                                    .into(imgAvatar);
                        }
                    }
            );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // ===== Init API =====
        userApi = ApiClient.getUserApi();

        // ===== Bind Views =====
        bindViews();

        // ===== Load Profile =====
        loadProfile();
        String savedAvatar = getSharedPreferences("profile", MODE_PRIVATE)
                .getString("avatar_uri", null);

        if (savedAvatar != null) {
            Glide.with(this)
                    .load(Uri.parse(savedAvatar))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(imgAvatar);
        }

        // ===== Init UI =====
        setupListeners();
        disableContinueButton();
    }

    // ==========================
    // BIND VIEWS
    // ==========================
    private void bindViews() {
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtLocation = findViewById(R.id.edtLocation);
        edtDob = findViewById(R.id.edtDob);
        edtGender = findViewById(R.id.edtGender);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
    }

    // ==========================
    // LISTENERS
    // ==========================
    private void setupListeners() {

        btnBack.setOnClickListener(v -> finish());

        btnEditAvatar.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*")
        );

        edtDob.setOnClickListener(v -> showDatePicker());

        edtGender.setOnClickListener(v -> showGenderDialog());

        btnContinue.setOnClickListener(v -> uploadAvatarThenUpdateProfile());


        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtFullName.addTextChangedListener(watcher);
        edtLocation.addTextChangedListener(watcher);
    }

    // ==========================
    // LOAD PROFILE
    // ==========================
    private void loadProfile() {
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindUser(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileSetupActivity.this,
                        "Không kết nối được server",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindUser(User user) {

        edtPhone.setText(user.getPhoneNumber());
        edtEmail.setText(user.getEmail());
        edtFullName.setText(user.getFullName());
        edtLocation.setText(user.getAddress());
        edtGender.setText(user.getGender());
        edtDob.setText(user.getDateOfBirth());

        if (user.getProfilePicture() != null) {
            try {
                if (user.getProfilePicture() != null) {
                    Glide.with(this)
                            .load(user.getProfilePicture())
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .into(imgAvatar);
                }

            } catch (Exception ignored) {}
        }

        validateInputs();
    }
    private void uploadAvatarThenUpdateProfile() {

        if (selectedAvatarUri == null) {
            updateProfile();
            return;
        }

        try {
            File file = uriToFile(selectedAvatarUri);

            RequestBody requestBody =
                    RequestBody.create(file, MediaType.parse("image/*"));

            MultipartBody.Part part =
                    MultipartBody.Part.createFormData(
                            "file",
                            file.getName(),
                            requestBody
                    );

            userApi.uploadAvatar(part).enqueue(new Callback<Map<String, String>>() {
                @Override
                public void onResponse(Call<Map<String, String>> call,
                                       Response<Map<String, String>> response) {

                    if (response.isSuccessful()) {
                        // backend đã lưu avatarUrl vào DB
                        updateProfile(); // chỉ update text
                    } else {
                        Toast.makeText(ProfileSetupActivity.this,
                                "Upload avatar thất bại",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, String>> call, Throwable t) {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Không upload được avatar",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File uriToFile(Uri uri) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = new File(getCacheDir(), "avatar_upload.jpg");

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    // ==========================
    // UPDATE PROFILE
    // ==========================
    private void updateProfile() {

        Map<String, Object> body = new HashMap<>();
        body.put("fullName", edtFullName.getText().toString().trim());
        body.put("gender", edtGender.getText().toString().trim());
        body.put("address", edtLocation.getText().toString().trim());
        body.put("dateOfBirth", edtDob.getText().toString().trim());

        userApi.updateProfile(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call,
                                   Response<Map<String, String>> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(ProfileSetupActivity.this,
                            "Cập nhật thành công",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Toast.makeText(ProfileSetupActivity.this,
                        "Cập nhật thất bại",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ==========================
    // DATE PICKER
    // ==========================
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(
                            "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth
                    );
                    edtDob.setText(date);
                    validateInputs();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ==========================
    // GENDER DIALOG
    // ==========================
    private void showGenderDialog() {
        String[] genders = {"Nam", "Nữ", "Khác"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Chọn giới tính")
                .setItems(genders, (dialog, which) -> {
                    edtGender.setText(genders[which]);
                    validateInputs();
                })
                .show();
    }

    // ==========================
    // VALIDATION
    // ==========================
    private void validateInputs() {
        boolean isValid =
                !edtFullName.getText().toString().trim().isEmpty() &&
                        !edtDob.getText().toString().trim().isEmpty() &&
                        !edtGender.getText().toString().trim().isEmpty();

        btnContinue.setEnabled(isValid);
        btnContinue.setBackgroundResource(
                isValid ? R.drawable.gb_btn_enable : R.drawable.bg_btn_signin
        );
    }

    private void disableContinueButton() {
        btnContinue.setEnabled(false);
        btnContinue.setBackgroundResource(R.drawable.bg_btn_signin);
    }
}
