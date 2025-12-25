package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CategoryItem;

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
        items.add(new CategoryItem("images/categories/ic_burger.png", "Burger"));
        items.add(new CategoryItem("images/categories/ic_taco.png", "Taco"));
        items.add(new CategoryItem("images/categories/ic_burrito.png", "Burrito"));
        items.add(new CategoryItem("images/categories/ic_drink.png", "Drink"));
        items.add(new CategoryItem("images/categories/ic_pizza.png", "Pizza"));
        items.add(new CategoryItem("images/categories/ic_donut.png", "Donut"));
        items.add(new CategoryItem("images/categories/ic_salad.png", "Salad"));
        items.add(new CategoryItem("images/categories/ic_noodles.png", "Noodles"));
        items.add(new CategoryItem("images/categories/ic_Sandwich.png", "Sandwich"));
        items.add(new CategoryItem("images/categories/ic_Pasta.png", "Pasta"));
        items.add(new CategoryItem("images/categories/ic_iceCream.png", "Ice Cream"));
        items.add(new CategoryItem("images/categories/ic_fried-rice.png", "Rice"));
        items.add(new CategoryItem("images/categories/ic_takoyaki.png", "Takoyaki"));
        items.add(new CategoryItem("images/categories/ic_fruits.png", "Fruit"));
        items.add(new CategoryItem("images/categories/ic_sausage.png", "Sausage"));
        items.add(new CategoryItem("images/categories/ic_goi-cuon.png", "Gỏi cuốn"));
        items.add(new CategoryItem("images/categories/ic_christmas-cookie.png", "Cookie"));
        items.add(new CategoryItem("images/categories/ic_pudding.png", "Pudding"));
        items.add(new CategoryItem("images/categories/ic_banhMi.png", "Bánh Mì"));
        items.add(new CategoryItem("images/categories/ic_dumpling.png", "Dumpling"));

        // Setup adapter với AssetManager
        adapter = new CategoryAdapter(items, item -> {
            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_categoryFragment_to_categoryProductsFragment);
        });
        rvCategories.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
