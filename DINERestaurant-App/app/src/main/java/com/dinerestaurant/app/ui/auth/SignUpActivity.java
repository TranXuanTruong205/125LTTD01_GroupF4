package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.StaticData;
import com.dinerestaurant.app.data.repository.AuthRepository;
import com.dinerestaurant.app.model.RegisterRequest;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText edtPhone, edtEmail, edtFullName;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 1️⃣ BIND VIEW TRƯỚC
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);
        btnRegister = findViewById(R.id.btnRegister);

        // 2️⃣ ĐỌC INTENT SAU KHI BIND VIEW
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("fromGoogle", false)) {

            String email = intent.getStringExtra("email");
            String fullName = intent.getStringExtra("fullName");

            // DEBUG (RẤT QUAN TRỌNG)
            Log.d("SIGNUP", "Email = " + email);
            Log.d("SIGNUP", "FullName = " + fullName);

            if (email != null) {
                edtEmail.setText(email);
                edtEmail.setEnabled(false);
                edtEmail.setFocusable(false);
                edtEmail.setClickable(false);
                edtEmail.setAlpha(0.6f);
            }

            if (fullName != null) {
                edtFullName.setText(fullName);
            }
            validateInputs();
        }

        // 3️⃣ CÒN LẠI GIỮ NGUYÊN
        btnRegister.setEnabled(false);
        btnRegister.setBackgroundResource(R.drawable.bg_btn_signin);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        edtPhone.addTextChangedListener(watcher);
        edtEmail.addTextChangedListener(watcher);
        edtFullName.addTextChangedListener(watcher);

        btnRegister.setOnClickListener(v -> registerRequest());

        // Click "Sign In" để quay lại màn Login
        findViewById(R.id.tvSignIn).setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    // =====================================================================
    // STEP 1: REQUEST REGISTER (SEND OTP)
    // =====================================================================
    private void registerRequest() {

        String phone = "84" + edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String fullName = edtFullName.getText().toString().trim();

        RegisterRequest request = new RegisterRequest(phone, email, fullName);

        new AuthRepository().registerRequest(request)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(SignUpActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String, Object> res = response.body();

                        // 1) Nếu có KEY "error" → báo lỗi
                        if (res.containsKey("error")) {
                            String errorMsg = res.get("error").toString();
                            Toast.makeText(SignUpActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            return; // không chuyển màn
                        }

                        // 2) Không có lỗi → đăng ký thành công

                        // Lưu dữ liệu chuẩn để sang OTP
                        StaticData.tempUser.setPhone(phone);
                        StaticData.tempUser.setEmail(email);
                        StaticData.tempUser.setFullName(fullName);
                        StaticData.isRegisterFlow = true;

                        // Chuyển sang màn xác minh OTP
                        Intent intent = new Intent(SignUpActivity.this, VerificationActivity.class);
                        intent.putExtra("phoneNumber", phone);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    // =====================================================================
    // Validate inputs + chuyển màu nút REGISTER
    // =====================================================================
    private void validateInputs() {
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String fullname = edtFullName.getText().toString().trim();

        // Vì bạn dùng (+84), người dùng CHỈ nhập 9 số còn lại.
        boolean validPhone = phone.length() == 9;
        boolean validEmail = !email.isEmpty();
        boolean validName = !fullname.isEmpty();

        boolean allValid = validPhone && validEmail && validName;

        btnRegister.setEnabled(allValid);
        btnRegister.setBackgroundResource(allValid ? R.drawable.gb_btn_enable : R.drawable.bg_btn_signin);
    }
}
