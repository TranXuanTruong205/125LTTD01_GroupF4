package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.User;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.UserApi;
import com.dinerestaurant.app.ui.MainActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetupActivity extends AppCompatActivity {

    EditText edtPhone, edtEmail, edtFullName, edtLocation;
    TextView edtDob, edtGender;
    Button btnContinue;
    ImageView btnBack;
    UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // ánh xạ view
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtLocation = findViewById(R.id.edtLocation);
        edtDob = findViewById(R.id.edtDob);
        edtGender = findViewById(R.id.edtGender);
        btnContinue = findViewById(R.id.btnContinue);
        btnBack = findViewById(R.id.btnBack);
        // init api
        userApi = ApiClient.getUserApi();

        // load profile từ backend
        loadProfile();

        // disable nút ban đầu
        btnContinue.setEnabled(false);
        btnContinue.setBackgroundResource(R.drawable.bg_btn_signin);

        // chọn ngày sinh
        edtDob.setOnClickListener(v -> showDatePicker());

        // chọn giới tính
        edtGender.setOnClickListener(v -> showGenderDialog());

        // text watcher
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtFullName.addTextChangedListener(watcher);
        edtLocation.addTextChangedListener(watcher);

        // nút continue
        btnContinue.setOnClickListener(v -> updateProfile());
        // nút back
        btnBack.setOnClickListener(v -> {
            finish();
        });

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

        if (user.getGender() != null)
            edtGender.setText(user.getGender());

        if (user.getAddress() != null)
            edtLocation.setText(user.getAddress());

        if (user.getDateOfBirth() != null)
            edtDob.setText(user.getDateOfBirth());
    }

    // ==========================
    // UPDATE PROFILE
    // ==========================
    private void updateProfile() {

        btnContinue.setEnabled(false);

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

                    Intent intent = new Intent(ProfileSetupActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    btnContinue.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                btnContinue.setEnabled(true);
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
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String formattedDate = String.format(
                            "%04d-%02d-%02d",
                            year, month + 1, dayOfMonth
                    );
                    edtDob.setText(formattedDate);

                    validateInputs();
                },
                y, m, d
        );
        dialog.show();
    }

    // ==========================
    // GENDER DIALOG
    // ==========================
    private void showGenderDialog() {
        String[] genders = {"Nam", "Nữ", "Khác"};

        new android.app.AlertDialog.Builder(this)
                .setTitle("Select Gender")
                .setItems(genders, (dialog, which) -> {
                    edtGender.setText(genders[which]);
                    validateInputs();
                })
                .show();
    }

    // ==========================
    // VALIDATE INPUT
    // ==========================
    private void validateInputs() {

        String fullName = edtFullName.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();

        boolean isValid =
                !fullName.isEmpty() &&
                        !dob.isEmpty() &&
                        !gender.isEmpty();

        btnContinue.setEnabled(isValid);

        if (isValid) {
            btnContinue.setBackgroundResource(R.drawable.gb_btn_enable);
        } else {
            btnContinue.setBackgroundResource(R.drawable.bg_btn_signin);
        }
    }
}
