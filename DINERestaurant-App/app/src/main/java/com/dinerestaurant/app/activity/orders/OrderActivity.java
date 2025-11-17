package com.dinerestaurant.app.activity.orders;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dinerestaurant.app.R;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Load OrderFragment khi Activity được tạo
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new OrderFragment())
                    .commit();
        }
    }
}