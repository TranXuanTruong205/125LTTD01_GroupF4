package com.dinerestaurant.app.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton; // Thêm import này
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.NavController;

import com.dinerestaurant.app.R;

// Tên class giả định bạn đã sửa thành NotificationFragment hoặc sử dụng tên đúng của bạn
public class NotificationFragement extends Fragment {

    public NotificationFragement() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // 1. Tìm ImageButton bằng ID mới
        ImageButton btnBack = view.findViewById(R.id.btnBackHeader);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Lấy NavController và quay lại màn hình trước đó trong back stack
                NavController navController = NavHostFragment.findNavController(this);
                navController.popBackStack();

                // HOẶC sử dụng hàm quay lại mặc định của Activity/Fragment Manager
                // requireActivity().onBackPressed();
            });
        }

        return view;
    }
}