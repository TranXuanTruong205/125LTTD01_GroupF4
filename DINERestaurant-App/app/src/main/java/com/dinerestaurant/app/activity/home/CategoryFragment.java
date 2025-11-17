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

        // Create categories data với ảnh từ assets
        List<CategoryItem> items = new ArrayList<>();
        items.add(new CategoryItem("images/categories/Cheese Burger.png", "Burger"));
        items.add(new CategoryItem("images/categories/Taco.png", "Taco"));
        items.add(new CategoryItem("images/categories/Burrito.png", "Burrito"));
        items.add(new CategoryItem("images/categories/Drink.png", "Drink"));
        items.add(new CategoryItem("images/categories/Pizza.png", "Pizza"));
        items.add(new CategoryItem("images/categories/Donut 2.png", "Donut"));
        items.add(new CategoryItem("images/categories/Salad.png", "Salad"));
        items.add(new CategoryItem("images/categories/Noodle 2.png", "Noodles"));
        items.add(new CategoryItem("images/categories/Sandwich.png", "Sandwich"));
        items.add(new CategoryItem("images/categories/Pasta.png", "Pasta"));
        items.add(new CategoryItem("images/categories/Ice cream.png", "Ice Cream"));
        items.add(new CategoryItem("images/categories/fried-rice.png", "Rice"));
        items.add(new CategoryItem("images/categories/takoyaki.png", "Takoyaki"));
        items.add(new CategoryItem("images/categories/fruits.png", "Fruit"));
        items.add(new CategoryItem("images/categories/sausage.png", "Sausage"));
        items.add(new CategoryItem("images/categories/goi-cuon.png", "Gỏi cuốn"));
        items.add(new CategoryItem("images/categories/christmas-cookie.png", "Cookie"));
        items.add(new CategoryItem("images/categories/pudding.png", "Pudding"));
        items.add(new CategoryItem("images/categories/banh-mi (1).png", "Bánh Mì"));
        items.add(new CategoryItem("images/categories/mandu.png", "Dumpling"));

        // Setup adapter với AssetManager
        adapter = new CategoryAdapter(items, requireContext().getAssets());
        rvCategories.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
