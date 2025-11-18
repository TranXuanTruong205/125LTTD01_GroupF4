package com.dinerestaurant.app.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinerestaurant.app.R;

public class VerificationActivity extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    TextView tvTimer, tvResend, tvSignIn, tvDescription;
    Button btnVerify;
    ImageView btnBack;

    CountDownTimer timer;
    int timeLeft = 45;   // 45s countdown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        initViews();
        setupOtpAutoMove();
        startCountdown();
        setupEvents();
    }

    private void initViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);

        tvTimer = findViewById(R.id.tvTimer);
        tvResend = findViewById(R.id.tvResend);
        tvSignIn = findViewById(R.id.tvSignIn);
        tvDescription = findViewById(R.id.tvDescription);

        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupEvents() {

        // BACK
        btnBack.setOnClickListener(v -> finish());

        // VERIFY
        btnVerify.setOnClickListener(v -> verifyOtp());

        // RESEND
        tvResend.setOnClickListener(v -> {
            if (timeLeft == 0) {
                Toast.makeText(this, "Code resent!", Toast.LENGTH_SHORT).show();
                resetCountdown();
            }
        });

        // BACK TO SIGN IN
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(VerificationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupOtpAutoMove() {

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (otp1.getText().length() == 1) otp2.requestFocus();
                if (otp2.getText().length() == 1) otp3.requestFocus();
                if (otp3.getText().length() == 1) otp4.requestFocus();
            }
        };

        otp1.addTextChangedListener(watcher);
        otp2.addTextChangedListener(watcher);
        otp3.addTextChangedListener(watcher);
        otp4.addTextChangedListener(watcher);
    }

    private void startCountdown() {
        tvResend.setTextColor(Color.parseColor("#B5B5B5")); // Gray (disabled)

        timer = new CountDownTimer(45000, 1000) { // 45 seconds
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = (int) (millisUntilFinished / 1000);
                tvTimer.setText(String.format("00 : %02d", timeLeft));
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                tvTimer.setText("00 : 00");
                tvResend.setTextColor(Color.parseColor("#FF6B4A")); // Orange = active
            }
        };
        timer.start();
    }

    private void resetCountdown() {
        if (timer != null) timer.cancel();
        timeLeft = 45;
        startCountdown();
    }

    private void verifyOtp() {
        String code = otp1.getText().toString().trim() +
                otp2.getText().toString().trim() +
                otp3.getText().toString().trim() +
                otp4.getText().toString().trim();

        if (code.length() != 4) {
            Toast.makeText(this, "Please enter full OTP code!", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Gửi code lên API để verify OTP

        Toast.makeText(this, "Verifying...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}
