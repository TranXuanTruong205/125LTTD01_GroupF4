package com.dinerestaurant.app.activity.other;

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

public class LikedFragment extends Fragment {

    private RecyclerView rvLikedProducts;
    private LikedProductAdapter adapter;

    public LikedFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liked, container, false);

        // Setup RecyclerView
        rvLikedProducts = view.findViewById(R.id.rvLikedProducts);
        rvLikedProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create sample data for liked products
        List<LikedProductItem> items = new ArrayList<>();
        items.add(new LikedProductItem("Chicken Burger", 4.9, "10.00", "6.00"));
        items.add(new LikedProductItem("Beef Burger", 4.9, "12.00", "10.00"));
        items.add(new LikedProductItem("Fish Burger", 4.9, "8.00", "8.00"));
        items.add(new LikedProductItem("Turkey Burger", 4.9, "7.50", "7.50"));
        items.add(new LikedProductItem("Lamb Burger", 4.9, "8.00", "8.00"));
        items.add(new LikedProductItem("Smoked Meat Burger", 4.9, "9.00", "9.00"));
        items.add(new LikedProductItem("Pork Burger", 4.9, "10.00", "10.00"));
        items.add(new LikedProductItem("Vegetarian Burger", 4.9, "8.50", "8.50"));

        // Setup adapter
        adapter = new LikedProductAdapter(items);
        rvLikedProducts.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}