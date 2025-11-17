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

        // Setup View All button to navigate to Special Offers
        view.findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view)
                .navigate(R.id.specialOffersFragment);
        });

        return view;
    }
}
