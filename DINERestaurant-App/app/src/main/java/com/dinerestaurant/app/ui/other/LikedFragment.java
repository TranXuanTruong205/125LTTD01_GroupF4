package com.dinerestaurant.app.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.adapter.other.LikedProductAdapter;
import com.dinerestaurant.app.model.other.LikedProductItem;

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

        // Create liked products data với ảnh từ assets
        List<LikedProductItem> items = new ArrayList<>();
        items.add(new LikedProductItem("images/liked_list/Image Burger.png", 
            "Chicken Burger", 4.9, "10.00", "6.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-1.png", 
            "Beef Burger", 4.9, "12.00", "10.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-2.png", 
            "Fish Burger", 4.9, "8.00", "8.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-3.png", 
            "Turkey Burger", 4.9, "7.50", "7.50"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-4.png", 
            "Lamb Burger", 4.9, "8.00", "8.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-5.png", 
            "Smoked Meat Burger", 4.9, "9.00", "9.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-6.png", 
            "Pork Burger", 4.9, "10.00", "10.00"));
        items.add(new LikedProductItem("images/liked_list/Image Burger-7.png", 
            "Vegetarian Burger", 4.9, "8.50", "8.50"));

        // Setup adapter với AssetManager
        adapter = new LikedProductAdapter(items, requireContext().getAssets());
        rvLikedProducts.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
