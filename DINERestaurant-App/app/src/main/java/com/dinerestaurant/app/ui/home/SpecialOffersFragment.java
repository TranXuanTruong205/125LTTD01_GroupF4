package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.SpecialOfferItem;

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

        // Create special offers data với ảnh từ assets
        List<SpecialOfferItem> items = new ArrayList<>();
        items.add(new SpecialOfferItem("images/special_offers/Image Burger.png", 
            "Chicken Burger", 4.9, 10.00, 6.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Burger-1.png", 
            "Beef Burger", 4.9, 12.00, 10.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Ramen Noodles.png", 
            "Ramen Noodles", 4.9, 22.00, 15.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Pho Noodles.png", 
            "Pho Noodles", 4.9, 24.00, 20.00));
        items.add(new SpecialOfferItem("images/special_offers/unsplash_V4MBq8kue3U.png", 
            "Fresh Fruit Donuts", 4.9, 6.00, 5.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Rotini.png", 
            "Rotini", 4.9, 20.00, 18.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Penne.png", 
            "Penne Pasta", 4.8, 15.00, 12.00));
        items.add(new SpecialOfferItem("images/special_offers/Image Farfalle.png", 
            "Farfalle Pasta", 4.7, 18.00, 14.00));

        // Setup adapter với AssetManager
        adapter = new SpecialOfferAdapter(items, requireContext().getAssets(),item->{
            // Xử lý khi click vào sản phẩm
            try {
                // 1. Nếu muốn truyền dữ liệu (tên món) sang màn hình kia thì dùng Bundle
                // Bundle bundle = new Bundle();
                // bundle.putString("product_name", item.getName());

                // 2. Chuyển màn hình
                Navigation.findNavController(view).navigate(
                        R.id.action_specialOffersFragment_to_productDetailFragment
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        rvSpecialOffers.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
