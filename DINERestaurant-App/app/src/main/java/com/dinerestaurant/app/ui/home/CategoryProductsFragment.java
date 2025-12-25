package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiService;
import com.dinerestaurant.app.data.remote.dto.MenuItemDto;
import com.dinerestaurant.app.model.CategoryProductItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryProductsFragment extends Fragment {

    private RecyclerView rvCategoryProducts;
    private CategoryProductAdapter adapter;
    private TextView tvCategoryName, tvCategoryIcon;

    private ApiService apiService;
    private int categoryId;
    private String categoryName;

    public CategoryProductsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_products, container, false);

        // Setup views
        tvCategoryName = view.findViewById(R.id.tvCategoryName);
        tvCategoryIcon = view.findViewById(R.id.tvCategoryIcon);
        rvCategoryProducts = view.findViewById(R.id.rvCategoryProducts);

        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Lấy arguments truyền từ HomeFragment
        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
            categoryName = getArguments().getString("categoryName", "Category");
        } else {
            categoryId = -1;
            categoryName = "Category";
        }

        tvCategoryName.setText(categoryName);

        apiService = ApiClient.getApiService();

        // Gọi API lấy sản phẩm theo Category
        loadProducts(view);

        view.findViewById(R.id.ivBack).setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void loadProducts(View rootView) {
        if (categoryId == -1) {
            Toast.makeText(getContext(), "Category not found", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getMenuItemsByCategory(categoryId)
                .enqueue(new Callback<List<MenuItemDto>>() {
                    @Override
                    public void onResponse(Call<List<MenuItemDto>> call,
                                           Response<List<MenuItemDto>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        List<CategoryProductItem> items = new ArrayList<>();

                        for (MenuItemDto dto : response.body()) {
                            String imagePath = mapMenuImage(dto, categoryName);

                            double price = dto.getPrice();
                            Double discount = dto.getDiscountPrice();

                            items.add(new CategoryProductItem(
                                    dto.getItemId(),
                                    imagePath,
                                    dto.getItemName(),
                                    dto.getDescription(),
                                    dto.getRating() != null ? dto.getRating() : 0.0,
                                    price,
                                    discount
                            ));

                        }

                        adapter = new CategoryProductAdapter(
                                items,
                                requireContext().getAssets(),
                                item -> {
                                    try {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("menu_item", item); // Đóng gói món ăn với key "menu_item"
                                        Navigation.findNavController(rootView)
                                                .navigate(R.id.action_categoryProductsFragment_to_productDetailFragment, bundle); // Gửi đi
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                        rvCategoryProducts.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String mapMenuImage(MenuItemDto dto, String categoryName) {

        if (categoryName == null) {
            return "images/special_offers/Image Burger.png"; // fallback
        }

        String lowerCat = categoryName.toLowerCase();

        if (lowerCat.contains("burger")) {
            return "images/burger_list/Image Burger.png";
        }
        if (lowerCat.contains("pizza")) {
            return "images/pizza_list/Image Pizza.png";
        }
        if (lowerCat.contains("taco")) {
            return "images/taco_list/Image Taco.png";
        }
        if (lowerCat.contains("burrito")) {
            return "images/burrito_list/Image Burrito.png";
        }
        if (lowerCat.contains("noodles") || lowerCat.contains("pho")) {
            return "images/noodles_list/Image Noodles.png";
        }
        if (lowerCat.contains("sandwich")) {
            return "images/sandwich_list/Image Sandwich.png";
        }
        if (lowerCat.contains("drink") || lowerCat.contains("nước") || lowerCat.contains("tea")) {
            return "images/drink_list/Image Drink.png";
        }
        if (lowerCat.contains("donut")) {
            return "images/donut_list/Image Donut.png";
        }
        if (lowerCat.contains("salad")) {
            return "images/salad_list/Image Salad.png";
        }
        if (lowerCat.contains("pasta")) {
            return "images/pasta_list/Image Pasta.png";
        }
        if (lowerCat.contains("ice cream") || lowerCat.contains("icecream") || lowerCat.contains("kem")) {
            return "images/icecream_list/Image IceCream.png";
        }

        // Mặc định: nếu không match category nào
        return "images/special_offers/Image Burger.png";
    }

}