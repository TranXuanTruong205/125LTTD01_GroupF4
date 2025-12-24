package com.dinerestaurant.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.ui.reservation.ReservationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private NavController nav;
    private LinearLayout bottomNavBar;

    ImageView homeIcon, orderIcon, scanIcon,reservationIcon, notifyIcon, profileIcon;
    TextView homeLabel, orderLabel, scanLabel,reservationLabel, notifyLabel, profileLabel;
    FrameLayout homeIconContainer, orderIconContainer, scanIconContainer,reservationIconContainer, notifyIconContainer, profileIconContainer;
    LinearLayout tabHome, tabOrder, tabScan,tabReservation, tabNotify, tabProfile;

    // Danh sách các Fragment ID sẽ hiển thị Bottom Bar
    private final Set<Integer> mainNavFragments = new HashSet<>(Arrays.asList(
            R.id.homeFragment,
            R.id.orderFragment,
            R.id.scanQRFragment,
            R.id.notificationFragment,
            R.id.profileFragment
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            nav = navHostFragment.getNavController();

            setupViews();
            setupNavigationVisibility();
            setupClicks();

            // Highlight tab tương ứng với màn hình khởi động (thường là homeFragment)
            int startDestinationId = nav.getGraph().getStartDestinationId();
            selectInitialTab(startDestinationId);
        }
    }

    private void selectInitialTab(int destinationId) {
        if (destinationId == R.id.homeFragment) selectTab(tabHome);
        else if (destinationId == R.id.orderFragment) selectTab(tabOrder);
        else if (destinationId == R.id.scanQRFragment) selectTab(tabScan);
        else if (destinationId == R.id.notificationFragment) selectTab(tabNotify);
        else if (destinationId == R.id.profileFragment) selectTab(tabProfile);
    }

    private void setupViews() {
        bottomNavBar = findViewById(R.id.customBottomBar);

        tabHome = findViewById(R.id.tabHome);
        tabOrder = findViewById(R.id.tabOrder);
        tabScan = findViewById(R.id.tabScan);
        tabReservation = findViewById(R.id.tabReservation);
        tabNotify = findViewById(R.id.tabNotify);
        tabProfile = findViewById(R.id.tabProfile);

        homeIconContainer = findViewById(R.id.homeIconContainer);
        orderIconContainer = findViewById(R.id.orderIconContainer);
        scanIconContainer = findViewById(R.id.scanIconContainer);
        reservationIconContainer = findViewById(R.id.reservationIconContainer);
        notifyIconContainer = findViewById(R.id.notifyIconContainer);
        profileIconContainer = findViewById(R.id.profileIconContainer);

        homeLabel = findViewById(R.id.homeLabel);
        orderLabel = findViewById(R.id.orderLabel);
        scanLabel = findViewById(R.id.scanLabel);
        reservationLabel = findViewById(R.id.reservationLabel);
        notifyLabel = findViewById(R.id.notifyLabel);
        profileLabel = findViewById(R.id.profileLabel);

        homeIcon = findViewById(R.id.homeIcon);
        orderIcon = findViewById(R.id.orderIcon);
        scanIcon = findViewById(R.id.scanIcon);
        reservationIcon = findViewById(R.id.reservationIcon);
        notifyIcon = findViewById(R.id.notifyIcon);
        profileIcon = findViewById(R.id.profileIcon);
    }

    private void setupNavigationVisibility() {
        nav.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (mainNavFragments.contains(destination.getId())) {
                bottomNavBar.setVisibility(View.VISIBLE);
                syncTabSelection(destination.getId());
            } else {
                bottomNavBar.setVisibility(View.GONE);
            }
        });
    }
    private void syncTabSelection(int destinationId) {
        // Dựa vào ID của màn hình đang hiển thị, chọn Tab tương ứng
        if (destinationId == R.id.homeFragment) {
            selectTab(tabHome);
        } else if (destinationId == R.id.orderFragment) {
            selectTab(tabOrder);
        } else if (destinationId == R.id.scanQRFragment) {
            selectTab(tabScan);
        } else if (destinationId == R.id.notificationFragment) {
            selectTab(tabNotify);
        } else if (destinationId == R.id.profileFragment) {
            selectTab(tabProfile);
        }
    }
    private void setupClicks() {
        tabHome.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabHome);
            nav.navigate(R.id.homeFragment);
        });

        tabOrder.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabOrder);
            nav.navigate(R.id.orderFragment);
        });

        tabScan.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabScan);
            nav.navigate(R.id.scanQRFragment);
        });

        tabReservation.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabReservation);
            // Mở màn ReservationActivity (vì bạn dùng Activity, không phải Fragment)
            startActivity(new Intent(MainActivity.this, ReservationActivity.class));
        });

        tabNotify.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabNotify);
            nav.navigate(R.id.notificationFragment);
        });

        tabProfile.setOnClickListener(v -> {
            clearSubScreensBackStack();
            selectTab(tabProfile);
            nav.navigate(R.id.profileFragment);
        });
    }

    private void selectTab(View selected) {
        // Reset màu icon về xám
        int inactiveColor = ContextCompat.getColor(this, R.color.icon_inactive);
        homeIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        orderIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        scanIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        reservationIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        notifyIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));
        profileIcon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_inactive));

        // Reset background
        homeIconContainer.setBackground(null);
        orderIconContainer.setBackground(null);
        scanIconContainer.setBackground(null);
        notifyIconContainer.setBackground(null);
        profileIconContainer.setBackground(null);
        reservationIconContainer.setBackground(null);
        // ẨN TEXT
        homeLabel.setVisibility(View.GONE);
        orderLabel.setVisibility(View.GONE);
        scanLabel.setVisibility(View.GONE);
        reservationLabel.setVisibility(View.GONE);
        notifyLabel.setVisibility(View.GONE);
        profileLabel.setVisibility(View.GONE);

        resetIconPosition();

        // Kích hoạt tab được chọn
        if (selected == tabHome) activateTab(homeIconContainer, homeIcon, homeLabel);
        else if (selected == tabOrder) activateTab(orderIconContainer, orderIcon, orderLabel);
        else if (selected == tabScan) activateTab(scanIconContainer, scanIcon, scanLabel);
        else if (selected == tabReservation) activateTab(reservationIconContainer, reservationIcon, reservationLabel);
        else if (selected == tabNotify) activateTab(notifyIconContainer, notifyIcon, notifyLabel);
        else if (selected == tabProfile) activateTab(profileIconContainer, profileIcon, profileLabel);
    }

    private void resetIconPosition() {
        int defaultMargin = dpToPx(12);
        setMargin(homeIconContainer, defaultMargin);
        setMargin(orderIconContainer, defaultMargin);
        setMargin(scanIconContainer, defaultMargin);
        setMargin(reservationIconContainer, defaultMargin);
        setMargin(notifyIconContainer, defaultMargin);
        setMargin(profileIconContainer, defaultMargin);
    }

    private void activateTab(FrameLayout iconContainer, ImageView icon, TextView label) {
        iconContainer.setBackgroundResource(R.drawable.nav_active_circle);
        icon.setImageTintList(ContextCompat.getColorStateList(this, R.color.icon_active));
        label.setVisibility(View.VISIBLE);
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

    private void clearSubScreensBackStack() {
        getSupportFragmentManager().popBackStack("PROFILE_SUB_SCREEN", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}