package com.dinerestaurant.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.TokenManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 👉 HIỂN THỊ GIAO DIỆN SPLASH
        setContentView(R.layout.activity_splash);

        // Init API
        ApiClient.init(getApplicationContext());

        // Delay để user thấy splash
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            TokenManager tokenManager = new TokenManager(this);
            String token = tokenManager.getToken();

            if (token != null) {
                // ĐÃ LOGIN → Main
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // CHƯA LOGIN → Login
                startActivity(new Intent(this, LoginActivity.class));
            }

            finish();

        }, 2000); // 2 giây (có thể chỉnh 1500)
    }
}
