package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CategoryProductItem;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductsFragment extends Fragment {

    private RecyclerView rvCategoryProducts;
    private CategoryProductAdapter adapter;
    private TextView tvCategoryName, tvCategoryIcon;

    public CategoryProductsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_products, container, false);

        // Setup views
        tvCategoryName = view.findViewById(R.id.tvCategoryName);
        tvCategoryIcon = view.findViewById(R.id.tvCategoryIcon);
        rvCategoryProducts = view.findViewById(R.id.rvCategoryProducts);

        // Setup RecyclerView
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create burger products data với ảnh từ assets
        List<CategoryProductItem> items = new ArrayList<>();
        items.add(new CategoryProductItem("images/burger_list/Image Burger.png", 
            "Chicken Burger", 4.9, "50.000", "40.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-1.png", 
            "Beef Burger", 4.9, "60.000", "55.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-2.png", 
            "Fish Burger", 4.9, "55.000", "55.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-3.png", 
            "Turkey Burger", 4.9, "45.000", "45.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-4.png", 
            "Lamb Burger", 4.9, "55.000", "55.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-5.png", 
            "Smoked Meat Burger", 4.9, "59.000", "59.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-6.png", 
            "Pork Burger", 4.9, "60.000", "60.000"));
        items.add(new CategoryProductItem("images/burger_list/Image Burger-7.png", 
            "Vegetarian Burger", 4.9, "50.000", "50.000"));

        // Setup adapter với AssetManager
        adapter = new CategoryProductAdapter(items, requireContext().getAssets(), item->{
            try {
                Navigation.findNavController(view).navigate(
                        R.id.action_categoryProductsFragment_to_productDetailFragment
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        rvCategoryProducts.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
