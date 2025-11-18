package com.dinerestaurant.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.ui.other.LikedFragment;
// Đã sửa MessageFragment thành Fragment, nên chúng ta dùng nó như một Fragment
import com.dinerestaurant.app.ui.other.MessageFragment;


public class ProfileFragment extends Fragment {

    // Khai báo biến cho Messages
    private LinearLayout btnMessages;

    // ID của vùng chứa Fragment trong MainActivity
    private static final int CONTAINER_ID = R.id.nav_host_fragment;
    // Thẻ (Tag) dùng để dọn dẹp Back Stack khi chuyển tab chính
    private static final String BACK_STACK_TAG = "PROFILE_SUB_SCREEN";

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout llMyLocations = view.findViewById(R.id.llMyLocations);
        // 1. Tìm LinearLayout của Liked
        LinearLayout llLiked = view.findViewById(R.id.llLiked);

        // 1.5. Ánh xạ btnmessages
        btnMessages = view.findViewById(R.id.btnmessages);

        // 2. Thiết lập OnClickListener cho My Locations
        llMyLocations.setOnClickListener(v -> {
            MyLocationsFragment myLocationsFragment = new MyLocationsFragment();
            replaceFragment(myLocationsFragment);
        });

        // 3. Thiết lập OnClickListener cho Liked
        llLiked.setOnClickListener(v -> {
            LikedFragment likedFragment = new LikedFragment();
            replaceFragment(likedFragment);
        });

        // 4. Thiết lập OnClickListener cho Messages (Đã sửa logic)
        setupMessagesButton();

        return view;
    }

    // 🚀 HÀM SETUP MESSAGES BUTTON ĐÃ SỬA
    private void setupMessagesButton() {
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                // SỬ DỤNG PHƯƠNG THỨC CHUYỂN FRAGMENT ĐÃ CÓ
                MessageFragment messageFragment = new MessageFragment();
                replaceFragment(messageFragment);
            });
        }
    }

    // Phương thức chung để xử lý việc chuyển sang màn hình phụ (Sub-Screen)
    private void replaceFragment(Fragment newFragment) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        // Thay thế Fragment trong vùng chứa chính
        transaction.replace(CONTAINER_ID, newFragment);

        // THÊM VÀO BACK STACK
        transaction.addToBackStack(BACK_STACK_TAG);

        transaction.commit();
    }
}