package com.dinerestaurant.app.activity.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private RecyclerView rvSpecialOffers;
    private CategoryAdapter categoryAdapter;
    private SpecialOfferAdapter specialOfferAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup Categories RecyclerView
        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        
        List<CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryItem("images/home_categories/Cheese Burger.png", "Burger"));
        categories.add(new CategoryItem("images/home_categories/Taco.png", "Taco"));
        categories.add(new CategoryItem("images/home_categories/Burrito.png", "Burrito"));
        categories.add(new CategoryItem("images/home_categories/Drink.png", "Drink"));
        categories.add(new CategoryItem("images/home_categories/Pizza.png", "Pizza"));
        categories.add(new CategoryItem("images/home_categories/Donut 2.png", "Donut"));
        categories.add(new CategoryItem("images/home_categories/Salad.png", "Salad"));
        categories.add(new CategoryItem("images/home_categories/Noodle 2.png", "Noodles"));
        categories.add(new CategoryItem("images/home_categories/Sandwich.png", "Sandwich"));
        categories.add(new CategoryItem("images/home_categories/Pasta.png", "Pasta"));
        categories.add(new CategoryItem("images/home_categories/Ice cream.png", "Ice Cream"));
        categories.add(new CategoryItem("images/home_categories/More 1.png", "More"));
        
        categoryAdapter = new CategoryAdapter(categories, requireContext().getAssets());
        rvCategories.setAdapter(categoryAdapter);

        // Setup Special Offers RecyclerView
        rvSpecialOffers = view.findViewById(R.id.rvSpecialOffers);
        rvSpecialOffers.setLayoutManager(new LinearLayoutManager(getContext(), 
            LinearLayoutManager.HORIZONTAL, false));
        
        List<SpecialOfferItem> offers = new ArrayList<>();
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger.png", 
            "Chicken Burger", 4.9, 10.00, 6.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger-1.png", 
            "Beef Burger", 4.9, 12.00, 10.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Ramen Noodles.png", 
            "Ramen Noodles", 4.9, 22.00, 15.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Pho Noodles.png", 
            "Pho Noodles", 4.9, 24.00, 20.00));
        
        specialOfferAdapter = new SpecialOfferAdapter(offers, requireContext().getAssets());
        rvSpecialOffers.setAdapter(specialOfferAdapter);

        // Setup View All button
        view.findViewById(R.id.tvViewAll).setOnClickListener(v -> {
            androidx.navigation.Navigation.findNavController(view)
                .navigate(R.id.specialOffersFragment);
        });

        return view;
    }
}
