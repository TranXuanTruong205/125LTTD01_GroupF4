package com.dinerestaurant.app.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edtPhone;
    private Button btnSignIn;
    private CheckBox chkRemember;
    private TextView tvRegister2;
    private ImageView btnGoogle, btnFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtPhone = findViewById(R.id.edtPhone);
        btnSignIn = findViewById(R.id.btnSignIn);
        chkRemember = findViewById(R.id.checkboxRemember);
        tvRegister2 = findViewById(R.id.tvRegister2);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
    }

    private void setupListeners() {

        // Nút đăng nhập chính
        btnSignIn.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Gửi OTP hoặc điều hướng sang màn hình OTP
            Toast.makeText(this, "Signing in: " + phone, Toast.LENGTH_SHORT).show();
        });

        // Text Register — chuyển màn hình đăng ký
        tvRegister2.setOnClickListener(v -> {
            Toast.makeText(this, "Navigate to Register screen", Toast.LENGTH_SHORT).show();

            // Ví dụ nếu có RegisterActivity:
            // startActivity(new Intent(this, RegisterActivity.class));
        });

        // Login bằng Google
        btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Login with Google", Toast.LENGTH_SHORT).show();
        });

        // Login bằng Facebook
        btnFacebook.setOnClickListener(v -> {
            Toast.makeText(this, "Login with Facebook", Toast.LENGTH_SHORT).show();
        });
    }
}
