package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Đã import đúng ImageView
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Load ảnh
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
    private TextView tvCategoryName;
    private ImageView ivCategoryIcon; // Dùng ImageView
    private ApiService apiService;
    private int categoryId;
    private String categoryName;
    private String categoryIconPath;
    public CategoryProductsFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_products, container, false);
        // Ánh xạ views
        tvCategoryName = view.findViewById(R.id.tvCategoryName);
        ivCategoryIcon = view.findViewById(R.id.tvCategoryIcon); // ID vẫn là tvCategoryIcon nhưng kiểu là ImageView
        rvCategoryProducts = view.findViewById(R.id.rvCategoryProducts);
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Nhận dữ liệu
        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
            categoryName = getArguments().getString("categoryName", "Category");
            categoryIconPath = getArguments().getString("categoryIcon"); // Nhận đường dẫn ảnh
        } else {
            categoryId = -1;
            categoryName = "Category";
        }
        tvCategoryName.setText(categoryName);
        // --- HIỂN THỊ ICON ---
        if (categoryIconPath != null && ivCategoryIcon != null) {
            Object imageSource = categoryIconPath;
            // Nếu không phải URL online -> thêm prefix load từ assets
            if (!categoryIconPath.startsWith("http")) {
                imageSource = "file:///android_asset/" + categoryIconPath;
            }

            Glide.with(this)
                    .load(imageSource)
                    .override(100, 100) // Resize nhẹ cho mượt
                    .error(android.R.drawable.ic_menu_gallery) // Ảnh lỗi
                    .into(ivCategoryIcon);
        } else if (ivCategoryIcon != null) {
            // Nếu không có path -> hiện icon mặc định
            ivCategoryIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        // ---------------------
        apiService = ApiClient.getApiService();
        loadProducts(view);

        view.findViewById(R.id.ivBack).setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }
    private void loadProducts(View rootView) {
        if (categoryId == -1) {
            return;
        }
        apiService.getMenuItemsByCategory(categoryId)
                .enqueue(new Callback<List<MenuItemDto>>() {
                    @Override
                    public void onResponse(Call<List<MenuItemDto>> call,
                                           Response<List<MenuItemDto>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }
                        List<CategoryProductItem> items = new ArrayList<>();
                        for (MenuItemDto dto : response.body()) {
                            // Lấy ảnh từ DB
                            String imagePath = dto.getImage();
                            if (imagePath == null || imagePath.isEmpty()) {
                                imagePath = "images/categories/ic_more.png";
                            }
                            items.add(new CategoryProductItem(
                                    dto.getItemId(),
                                    imagePath,
                                    dto.getItemName(),
                                    dto.getDescription(),
                                    dto.getRating() != null ? dto.getRating() : 0.0,
                                    dto.getPrice(),
                                    dto.getDiscountPrice()
                            ));
                        }
                        // Code này y chang, chỉ đảm bảo adapter đã fix Glide
                        adapter = new CategoryProductAdapter(
                                items,
                                requireContext().getAssets(),
                                item -> {
                                    try {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("menu_item", item);
                                        Navigation.findNavController(rootView)
                                                .navigate(R.id.action_categoryProductsFragment_to_productDetailFragment, bundle);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                        rvCategoryProducts.setAdapter(adapter);
                    }
                    @Override
                    public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                    }
                });
    }
}