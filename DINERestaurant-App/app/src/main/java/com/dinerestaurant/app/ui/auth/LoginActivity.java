package com.dinerestaurant.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.StaticData;
import com.dinerestaurant.app.data.repository.AuthRepository;
import com.dinerestaurant.app.model.LoginRequest;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtPhone;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);

        // Mặc định: xám, không click được
        btnSignIn.setEnabled(false);
        btnSignIn.setBackgroundResource(R.drawable.bg_btn_signin);
        btnSignIn.setAlpha(1f);

        // Lắng nghe nhập số điện thoại
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                // Kiểm tra 9 số (do đã có +84)
                boolean valid = phone.length() == 9;

                if (valid) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.gb_btn_enable); // màu cam đậm
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.bg_btn_signin); // màu xám
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click Sign In
        btnSignIn.setOnClickListener(v -> {
            String phone = "84"+edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            // request gửi OTP
            LoginRequest request = new LoginRequest(phone);

            new AuthRepository().loginRequestOtp(request)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                                intent.putExtra("phoneNumber", phone);
                                startActivity(intent);

                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Phone number not found! Please register.", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            Toast.makeText(LoginActivity.this,
                                    "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        findViewById(R.id.tvRegister2).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }
}