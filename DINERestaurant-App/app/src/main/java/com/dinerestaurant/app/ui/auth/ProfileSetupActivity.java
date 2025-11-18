package com.dinerestaurant.app.ui.auth;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import com.dinerestaurant.app.R;

import java.util.Calendar;

public class ProfileSetupActivity extends AppCompatActivity {

    ImageView btnBack, imgAvatar, btnEditAvatar, btnCalendar, btnGenderDropdown, btnLocation;
    EditText edtPhone, edtEmail, edtFullName, edtDob, edtLocation;
    TextView edtGender;
    Button btnContinue;

    Uri avatarUri;

    ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        initViews();
        initImagePicker();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);

        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        edtDob = findViewById(R.id.edtDob);
        edtGender = findViewById(R.id.edtGender);
        edtLocation = findViewById(R.id.edtLocation);

        btnCalendar = findViewById(R.id.btnCalendar);
        btnGenderDropdown = findViewById(R.id.btnGenderDropdown);
        btnLocation = findViewById(R.id.btnLocation);

        btnContinue = findViewById(R.id.btnContinue);
    }

    private void initImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        avatarUri = uri;
                        imgAvatar.setImageURI(uri);
                    }
                }
        );
    }

    private void setupEvents() {

        // Back
        btnBack.setOnClickListener(v -> finish());

        // Chọn ảnh đại diện
        btnEditAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Chọn ngày sinh
        btnCalendar.setOnClickListener(v -> showDatePicker());
        edtDob.setOnClickListener(v -> showDatePicker());

        // Chọn giới tính bằng AlertDialog
        btnGenderDropdown.setOnClickListener(v -> showGenderDialog());
        edtGender.setOnClickListener(v -> showGenderDialog());

        // Continue
        btnContinue.setOnClickListener(v -> validateAndContinue());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int year = 2000, month = 0, day = 1;

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> edtDob.setText(d + "/" + (m + 1) + "/" + y),
                year, month, day
        );

        dialog.show();
    }

    private void showGenderDialog() {
        String[] genders = {"Male", "Female", "Other"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");

        builder.setItems(genders, (dialog, which) -> {
            edtGender.setText(genders[which]);
        });

        builder.show();
    }

    private void validateAndContinue() {
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String name = edtFullName.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();

        if (phone.isEmpty()) { edtPhone.setError("Enter phone number"); return; }
        if (email.isEmpty()) { edtEmail.setError("Enter email"); return; }
        if (name.isEmpty()) { edtFullName.setError("Enter full name"); return; }
        if (dob.isEmpty()) { Toast.makeText(this, "Select birthday", Toast.LENGTH_SHORT).show(); return; }
        if (gender.isEmpty() || gender.equals("Gender")) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Gửi API cập nhật profile
        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
    }
}
