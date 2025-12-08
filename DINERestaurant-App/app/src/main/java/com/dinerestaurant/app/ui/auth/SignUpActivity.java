package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.StaticData;

public class SignUpActivity extends AppCompatActivity {

    EditText edtPhone, edtEmail, edtFullName;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        btnRegister = findViewById(R.id.btnRegister);

        // Mặc định: disable + màu nhạt
        btnRegister.setEnabled(false);
        btnRegister.setBackgroundResource(R.drawable.bg_btn_signin);
        btnRegister.setAlpha(1f);

        // Bắt sự thay đổi của 3 ô input
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        edtPhone.addTextChangedListener(watcher);
        edtEmail.addTextChangedListener(watcher);
        edtFullName.addTextChangedListener(watcher);

        // Nhấn REGISTER
        btnRegister.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String fullName = edtFullName.getText().toString().trim();

            // Lưu dữ liệu vào StaticData.tempUser
            StaticData.tempUser.setPhone(phone);
            StaticData.tempUser.setEmail(email);
            StaticData.tempUser.setFullName(fullName);

            // Đánh dấu đây là luồng đăng ký
            StaticData.isRegisterFlow = true;

            // Đi đến OTP
            startActivity(new Intent(this, VerificationActivity.class));
        });

        // Quay lại login
        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());
    }


    // -----------------------------------------------------------------
    //      Validate inputs + Đổi màu nút Register (ENABLE / DISABLE)
    // -----------------------------------------------------------------
    private void validateInputs() {
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String fullname = edtFullName.getText().toString().trim();

        // Vì bạn dùng (+84), người dùng CHỈ nhập 9 số còn lại.
        boolean validPhone = phone.length() == 9;
        boolean validEmail = !email.isEmpty();
        boolean validName = !fullname.isEmpty();

        boolean allValid = validPhone && validEmail && validName;

        if (allValid) {
            btnRegister.setEnabled(true);
            btnRegister.setAlpha(1f); // không bị mờ
            btnRegister.setBackgroundResource(R.drawable.gb_btn_enable); // cam đậm
        } else {
            btnRegister.setEnabled(false);
            btnRegister.setAlpha(1f);
            btnRegister.setBackgroundResource(R.drawable.bg_btn_signin); // nhạt
        }
    }
}
