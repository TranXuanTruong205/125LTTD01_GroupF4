package com.dinerestaurant.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Thêm import ImageView
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager; // Thêm import FragmentManager

import com.dinerestaurant.app.R;

public class MyLocationsFragment extends Fragment {

    public MyLocationsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_locations, container, false);

        // 1. Tìm nút Quay về (Back Button)
        ImageView btnBack = view.findViewById(R.id.btnBack);

        // 2. Thiết lập OnClickListener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy FragmentManager
                FragmentManager fm = requireActivity().getSupportFragmentManager();

                // Gọi popBackStack() để quay lại Fragment trước đó trong Back Stack.
                // Vì bạn đã thêm ProfileFragment vào Back Stack khi chuyển sang MyLocationsFragment,
                // lệnh này sẽ đưa bạn trở lại ProfileFragment.
                fm.popBackStack();
            }
        });

        return view;
    }
}