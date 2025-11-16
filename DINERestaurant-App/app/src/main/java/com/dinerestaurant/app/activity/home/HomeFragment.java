package com.dinerestaurant.app.activity.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.dinerestaurant.app.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Gán layout XML vào Fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Nếu muốn, ánh xạ view:
        // TextView tvTitle = view.findViewById(R.id.tvTitleHome);

        return view;
    }
}
