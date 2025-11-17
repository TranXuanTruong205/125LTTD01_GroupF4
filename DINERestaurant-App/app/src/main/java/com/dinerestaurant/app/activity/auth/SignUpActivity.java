package com.dinerestaurant.app.activity.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinerestaurant.app.R;

public class SignUpActivity extends AppCompatActivity {

    EditText edtPhone, edtEmail, edtFullName;
    CheckBox checkboxRemember;
    Button btnRegister;
    ImageView btnGoogle, btnFacebook;
    TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();
        setupEvents();
    }

    private void initViews() {
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtFullName = findViewById(R.id.edtFullName);

        checkboxRemember = findViewById(R.id.checkboxRemember);

        btnRegister = findViewById(R.id.btnRegister);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);

        tvSignIn = findViewById(R.id.tvSignIn);
    }

    private void setupEvents() {

        // Nhấn nút Register
        btnRegister.setOnClickListener(v -> doRegister());

        // Click "Sign In" → quay lại LoginActivity
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Social login (demo)
        btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Google Login (demo)", Toast.LENGTH_SHORT).show()
        );

        btnFacebook.setOnClickListener(v ->
                Toast.makeText(this, "Facebook Login (demo)", Toast.LENGTH_SHORT).show()
        );
    }

    private void doRegister() {
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String fullname = edtFullName.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Please enter phone number");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Please enter email");
            return;
        }
        if (TextUtils.isEmpty(fullname)) {
            edtFullName.setError("Please enter full name");
            return;
        }

        boolean remember = checkboxRemember.isChecked();

        // TODO: Gửi API đăng ký tại đây (sau này bạn bổ sung)
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

        // Chuyển sang LoginActivity
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
