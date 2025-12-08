package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.StaticData;
import com.dinerestaurant.app.ui.MainActivity;

public class VerificationActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerify);

        // Mặc định: disable + màu xám
        btnVerify.setEnabled(false);
        btnVerify.setBackgroundResource(R.drawable.bg_btn_signin);
        btnVerify.setAlpha(1f);

        // Watcher để kiểm tra OTP
        TextWatcher otpWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkOtpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        otp1.addTextChangedListener(otpWatcher);
        otp2.addTextChangedListener(otpWatcher);
        otp3.addTextChangedListener(otpWatcher);
        otp4.addTextChangedListener(otpWatcher);

        // Nhấn VERIFY
        btnVerify.setOnClickListener(v -> {
            String code = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();

            if (!code.equals(StaticData.STATIC_OTP)) {
                Toast.makeText(this, "Incorrect OTP!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (StaticData.isRegisterFlow) {
                // Tiếp tục luồng đăng ký
                startActivity(new Intent(this, ProfileSetupActivity.class));
                finish(); // Đóng VerificationActivity
            } else {
                // Đăng nhập thành công -> Chuyển sang MainActivity
                Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, MainActivity.class);
                // Cờ quan trọng: Xóa back stack để không quay lại được màn hình Login/Verification
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                finish(); // Đóng Activity hiện tại ngay lập tức
            }
        });

        // Quay về login/back
        findViewById(R.id.tvSignIn).setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void checkOtpInputs() {
        String o1 = otp1.getText().toString();
        String o2 = otp2.getText().toString();
        String o3 = otp3.getText().toString();
        String o4 = otp4.getText().toString();

        boolean valid = o1.length() == 1
                && o2.length() == 1
                && o3.length() == 1
                && o4.length() == 1;

        if (valid) {
            btnVerify.setEnabled(true);
            btnVerify.setAlpha(1f);
            btnVerify.setBackgroundResource(R.drawable.gb_btn_enable); // cam đậm
        } else {
            btnVerify.setEnabled(false);
            btnVerify.setAlpha(1f);
            btnVerify.setBackgroundResource(R.drawable.bg_btn_signin); // xám
        }
    }
}