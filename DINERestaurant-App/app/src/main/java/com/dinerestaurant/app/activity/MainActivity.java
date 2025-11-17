package com.dinerestaurant.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.dinerestaurant.app.R;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private NavController nav;

    ImageView homeIcon, orderIcon, scanIcon, notifyIcon, profileIcon;
    TextView homeLabel, orderLabel, scanLabel, notifyLabel, profileLabel;
    FrameLayout homeIconContainer, orderIconContainer, scanIconContainer, notifyIconContainer, profileIconContainer;
    LinearLayout tabHome, tabOrder, tabScan, tabNotify, tabProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        nav = navHostFragment.getNavController();

        setupViews();
        setupClicks();

        selectTab(tabHome);
        nav.navigate(R.id.homeFragment);
    }

    private void setupViews() {

        tabHome = findViewById(R.id.tabHome);
        tabOrder = findViewById(R.id.tabOrder);
        tabScan = findViewById(R.id.tabScan);
        tabNotify = findViewById(R.id.tabNotify);
        tabProfile = findViewById(R.id.tabProfile);

        homeIconContainer = findViewById(R.id.homeIconContainer);
        orderIconContainer = findViewById(R.id.orderIconContainer);
        scanIconContainer = findViewById(R.id.scanIconContainer);
        notifyIconContainer = findViewById(R.id.notifyIconContainer);
        profileIconContainer = findViewById(R.id.profileIconContainer);

        homeLabel = findViewById(R.id.homeLabel);
        orderLabel = findViewById(R.id.orderLabel);
        scanLabel = findViewById(R.id.scanLabel);
        notifyLabel = findViewById(R.id.notifyLabel);
        profileLabel = findViewById(R.id.profileLabel);

        homeIcon = findViewById(R.id.homeIcon);
        orderIcon = findViewById(R.id.orderIcon);
        scanIcon = findViewById(R.id.scanIcon);
        notifyIcon = findViewById(R.id.notifyIcon);
        profileIcon = findViewById(R.id.profileIcon);
    }


    private void setupClicks() {
        tabHome.setOnClickListener(v -> {
            selectTab(tabHome);
            nav.navigate(R.id.homeFragment);
        });

        tabOrder.setOnClickListener(v -> {
            selectTab(tabOrder);
            nav.navigate(R.id.orderFragment);
        });

        tabScan.setOnClickListener(v -> {
            selectTab(tabScan);
            nav.navigate(R.id.scanQRFragment);
        });

        tabNotify.setOnClickListener(v -> {
            selectTab(tabNotify);
            nav.navigate(R.id.notificationFragment);
        });

        tabProfile.setOnClickListener(v -> {
            selectTab(tabProfile);
            nav.navigate(R.id.profileFragment);
        });
    }

    private void selectTab(View selected) {

        // Reset icon về màu xám
        homeIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        orderIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        scanIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        notifyIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        profileIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));

        // Reset background
        homeIconContainer.setBackground(null);
        orderIconContainer.setBackground(null);
        scanIconContainer.setBackground(null);
        notifyIconContainer.setBackground(null);
        profileIconContainer.setBackground(null);

        // ẨN TẤT CẢ TEXT
        homeLabel.setVisibility(View.GONE);
        orderLabel.setVisibility(View.GONE);
        scanLabel.setVisibility(View.GONE);
        notifyLabel.setVisibility(View.GONE);
        profileLabel.setVisibility(View.GONE);

        // ĐẨY ICON LÊN LẠI (default)
        resetIconPosition();

        // --- ACTIVE TAB ---
        if (selected == tabHome) {
            activateTab(homeIconContainer, homeIcon, homeLabel);

        } else if (selected == tabOrder) {
            activateTab(orderIconContainer, orderIcon, orderLabel);

        } else if (selected == tabScan) {
            activateTab(scanIconContainer, scanIcon, scanLabel);

        } else if (selected == tabNotify) {
            activateTab(notifyIconContainer, notifyIcon, notifyLabel);

        } else if (selected == tabProfile) {
            activateTab(profileIconContainer, profileIcon, profileLabel);
        }
    }


    // --- Tạo hàm hỗ trợ ---
    private void resetIconPosition() {
        int defaultMargin = dpToPx(12);

        setMargin(homeIconContainer, defaultMargin);
        setMargin(orderIconContainer, defaultMargin);
        setMargin(scanIconContainer, defaultMargin);
        setMargin(notifyIconContainer, defaultMargin);
        setMargin(profileIconContainer, defaultMargin);
    }

    private void activateTab(FrameLayout iconContainer, ImageView icon, TextView label) {

        iconContainer.setBackgroundResource(R.drawable.nav_active_circle);
        icon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_active));

        label.setVisibility(View.VISIBLE);

        // icon bị đẩy xuống để nhường chỗ cho text
        setMargin(iconContainer, 0);
    }

    private void setMargin(FrameLayout v, int bottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) v.getLayoutParams();
        params.bottomMargin = bottom;
        v.setLayoutParams(params);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


}
