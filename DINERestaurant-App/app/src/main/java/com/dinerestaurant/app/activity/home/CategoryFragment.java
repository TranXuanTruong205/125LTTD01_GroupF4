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

public class CategoryFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;

    public CategoryFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Setup RecyclerView
        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));

        // Create categories data
        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem("🍔", "Burger"));
        items.add(new CategoryItem("🌮", "Taco"));
        items.add(new CategoryItem("🌯", "Burrito"));
        items.add(new CategoryItem("🥤", "Drink"));
        items.add(new CategoryItem("🍕", "Pizza"));
        items.add(new CategoryItem("🍩", "Donut"));
        items.add(new CategoryItem("🥗", "Salad"));
        items.add(new CategoryItem("🍜", "Noodles"));
        items.add(new CategoryItem("🥪", "Sandwich"));
        items.add(new CategoryItem("🍝", "Pasta"));
        items.add(new CategoryItem("🍦", "Ice Cream"));
        items.add(new CategoryItem("🍚", "Rice"));
        items.add(new CategoryItem("🍱", "Takoyaki"));
        items.add(new CategoryItem("🍓", "Fruit"));
        items.add(new CategoryItem("🌭", "Sausage"));
        items.add(new CategoryItem("🌍", "Gỏi cuốn"));
        items.add(new CategoryItem("🍪", "Cookie"));
        items.add(new CategoryItem("🍮", "Pudding"));
        items.add(new CategoryItem("🥖", "Bánh Mì"));
        items.add(new CategoryItem("🥟", "Dumpling"));

        // Setup adapter
        adapter = new CategoryAdapter(items);
        rvCategories.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}