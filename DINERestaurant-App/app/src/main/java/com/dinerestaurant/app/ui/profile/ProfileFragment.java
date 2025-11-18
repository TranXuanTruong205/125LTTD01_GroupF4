package com.dinerestaurant.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dinerestaurant.app.R;
// Import thêm LikedFragment (đang nằm trong package com.dinerestaurant.app.ui.other)
import com.dinerestaurant.app.ui.other.LikedFragment;

public class ProfileFragment extends Fragment {

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

        // 2. Thiết lập OnClickListener cho My Locations (Logic cũ)
        llMyLocations.setOnClickListener(v -> {
            MyLocationsFragment myLocationsFragment = new MyLocationsFragment();
            replaceFragment(myLocationsFragment);
        });

        // 3. Thiết lập OnClickListener cho Liked (Logic mới)
        llLiked.setOnClickListener(v -> {
            LikedFragment likedFragment = new LikedFragment(); // Khởi tạo LikedFragment
            replaceFragment(likedFragment);
        });

        return view;
    }

    // Phương thức chung để xử lý việc chuyển sang màn hình phụ (Sub-Screen)
    private void replaceFragment(Fragment newFragment) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        // Thay thế Fragment trong vùng chứa chính
        transaction.replace(CONTAINER_ID, newFragment);

        // THÊM VÀO BACK STACK để:
        // a) Cho phép nút Back vật lý/trên màn hình hoạt động.
        // b) Cho phép logic dọn dẹp Back Stack trong MainActivity hoạt động khi chuyển tab.
        transaction.addToBackStack(BACK_STACK_TAG);

        transaction.commit();
    }
}