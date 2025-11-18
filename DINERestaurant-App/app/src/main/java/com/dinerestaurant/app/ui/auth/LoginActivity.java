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
import com.dinerestaurant.app.data.StaticData;
import com.google.android.material.button.MaterialButton;

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
        btnSignIn.setAlpha(1f);   // tránh MaterialButton tự làm mờ

        // Lắng nghe nhập số điện thoại
        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();

                // 👉 Vì bạn đã có (+84) riêng, nên ở đây chỉ kiểm tra 9 số
                boolean valid = phone.length() == 9;

                if (valid) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.gb_btn_enable);   // màu cam đậm
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setAlpha(1f);
                    btnSignIn.setBackgroundResource(R.drawable.bg_btn_signin);   // màu cam/xám nhạt
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click Sign In
        btnSignIn.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            // lưu phone vào dữ liệu tĩnh
            StaticData.tempUser.setPhone(phone);
            StaticData.isRegisterFlow = false;

            startActivity(new Intent(this, VerificationActivity.class));
        });

        // Chuyển sang màn đăng ký
        findViewById(R.id.tvRegister2).setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class))
        );
    }
}
