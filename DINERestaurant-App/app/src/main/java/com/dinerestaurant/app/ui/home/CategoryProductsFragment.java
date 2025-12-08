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
import com.dinerestaurant.app.model.CategoryProductItem;

import java.util.ArrayList;
import java.util.List;

import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiService;
import com.dinerestaurant.app.data.remote.dto.MenuItemDto;

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

    public CategoryProductsFragment() { }

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

        apiService = ApiClient.getClient().create(ApiService.class);

        // Gọi API lấy sản phẩm theo Category
        loadProducts(view);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
    private void loadProducts(View rootView) {
        if (categoryId == -1) {
            Toast.makeText(getContext(), "Category not found", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getMenuItemsByCategory(categoryId).enqueue(new Callback<List<MenuItemDto>>() {
            @Override
            public void onResponse(Call<List<MenuItemDto>> call, Response<List<MenuItemDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<MenuItemDto> dtoList = response.body();
                List<CategoryProductItem> items = new ArrayList<>();

                int index = 0;
                for (MenuItemDto dto : dtoList) {

                    // TẠM THỜI: dùng assets cũ cho hình ảnh hoặc để null
                    String imagePath = null;
                    // Nếu về sau bạn map được dto.getImage() với file assets thì sửa tại đây
                    double price = dto.getPrice();
                    Double discount = dto.getDiscountPrice();
                    items.add(new CategoryProductItem(
                            dto.getItemId(),          // id
                            imagePath,                // imagePath (tạm null / sau map ảnh)
                            dto.getItemName(),        // name
                            dto.getRating() != null ? dto.getRating() : 0.0, // rating
                            price,                    // price
                            discount                  // discountPrice
                    ));
                    index++;
                }

                adapter = new CategoryProductAdapter(
                        items,
                        requireContext().getAssets(),
                        item -> {
                            try {
                                Navigation.findNavController(rootView)
                                        .navigate(R.id.action_categoryProductsFragment_to_productDetailFragment);
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
                t.printStackTrace();
            }
        });
    }

}
