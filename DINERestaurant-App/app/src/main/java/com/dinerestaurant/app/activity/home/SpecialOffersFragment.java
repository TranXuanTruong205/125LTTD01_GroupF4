package com.dinerestaurant.app.activity.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import java.util.ArrayList;
import java.util.List;

public class SpecialOffersFragment extends Fragment {

    private RecyclerView rvSpecialOffers;
    private SpecialOfferAdapter adapter;

    public SpecialOffersFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_special_offers, container, false);

        // Setup RecyclerView
        rvSpecialOffers = view.findViewById(R.id.rvSpecialOffers);
        rvSpecialOffers.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create sample data
        List<SpecialOfferItem> items = new ArrayList<>();
        items.add(new SpecialOfferItem("Chicken Burger", 4.9, 10.00, 6.00));
        items.add(new SpecialOfferItem("Beef Burger", 4.9, 12.00, 10.00));
        items.add(new SpecialOfferItem("Ramen Noodles", 4.9, 22.00, 15.00));
        items.add(new SpecialOfferItem("Pho Noodles", 4.9, 24.00, 20.00));
        items.add(new SpecialOfferItem("Fresh Fruit Donuts", 4.9, 6.00, 5.00));
        items.add(new SpecialOfferItem("Rotini", 4.9, 20.00, 18.00));
        items.add(new SpecialOfferItem("Pizza Margherita", 4.8, 15.00, 12.00));
        items.add(new SpecialOfferItem("Caesar Salad", 4.7, 8.00, 6.50));

        // Setup adapter
        adapter = new SpecialOfferAdapter(items);
        rvSpecialOffers.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}