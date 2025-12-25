package com.dinerestaurant.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dinerestaurant.app.data.local.TokenManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiClient.init(getApplicationContext());
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();

        if (token != null) {
            // ĐÃ LOGIN → vào thẳng Main
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // CHƯA LOGIN
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}
