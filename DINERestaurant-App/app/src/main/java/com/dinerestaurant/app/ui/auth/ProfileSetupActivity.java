package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.StaticData;

import java.util.Calendar;


public class ProfileSetupActivity extends AppCompatActivity {

    EditText edtPhone, edtEmail, edtFullName, edtLocation;
    TextView edtDob, edtGender;
    Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // ánh xạ
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtLocation = findViewById(R.id.edtLocation);

        edtDob = findViewById(R.id.edtDob);
        edtGender = findViewById(R.id.edtGender);
        btnContinue = findViewById(R.id.btnContinue);

        // Gán dữ liệu ban đầu
        edtPhone.setText(StaticData.tempUser.getPhone());
        edtEmail.setText(StaticData.tempUser.getEmail());
        edtFullName.setText(StaticData.tempUser.getFullName());

        // Mặc định: nút xám (disabled)
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(1f);
        btnContinue.setBackgroundResource(R.drawable.bg_btn_signin);

        // CLICK DOB → DatePicker
        edtDob.setOnClickListener(v -> showDatePicker());

        // CLICK Gender → Dialog chọn gender
        edtGender.setOnClickListener(v -> showGenderDialog());

        // Listen thay đổi nhập liệu
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtFullName.addTextChangedListener(watcher);
        edtLocation.addTextChangedListener(watcher);

        // Nút Continue
        btnContinue.setOnClickListener(v -> {
            StaticData.currentUser.setPhone(edtPhone.getText().toString());
            StaticData.currentUser.setEmail(edtEmail.getText().toString());
            StaticData.currentUser.setFullName(edtFullName.getText().toString());
            StaticData.currentUser.setDob(edtDob.getText().toString());
            StaticData.currentUser.setGender(edtGender.getText().toString());
            StaticData.currentUser.setLocation(edtLocation.getText().toString());

            Toast.makeText(this, "Profile Completed!", Toast.LENGTH_SHORT).show();
        });
    }

    // ==========================
    // CHỌN NGÀY SINH
    // ==========================
    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    edtDob.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    validateInputs();
                },
                y, m, d
        );
        dialog.show();
    }

    // ==========================
    // CHỌN GIỚI TÍNH
    // ==========================
    private void showGenderDialog() {
        String[] genders = {"Male", "Female", "Other"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        builder.setItems(genders, (dialog, which) -> {
            edtGender.setText(genders[which]);
            validateInputs();
        });
        builder.show();
    }

    // ==========================
    // ENABLE / DISABLE BUTTON
    // ==========================
    private void validateInputs() {

        String fullName = edtFullName.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();

        // tất cả phải có dữ liệu
        boolean isValid =
                !fullName.isEmpty() &&
                        !dob.isEmpty() &&
                        !gender.isEmpty();

        if (isValid) {
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1f);
            btnContinue.setBackgroundResource(R.drawable.gb_btn_enable);
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setAlpha(1f);
            btnContinue.setBackgroundResource(R.drawable.bg_btn_signin);
        }
    }
}