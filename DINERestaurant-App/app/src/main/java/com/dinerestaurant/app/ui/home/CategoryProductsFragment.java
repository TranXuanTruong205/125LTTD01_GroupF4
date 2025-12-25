package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
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
    private TextView tvCategoryName;
    private ApiService apiService;
    private int categoryId;
    private String categoryName;
    public CategoryProductsFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_products, container, false);
        if (getArguments() != null) {
            categoryId = getArguments().getInt("categoryId", -1);
            categoryName = getArguments().getString("categoryName", "Danh mục");
        }
        tvCategoryName = view.findViewById(R.id.tvCategoryName);
        tvCategoryName.setText(categoryName);

        rvCategoryProducts = view.findViewById(R.id.rvCategoryProducts);
        rvCategoryProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // --- PHẦN QUAN TRỌNG: CHUYỂN TRANG ---
        adapter = new CategoryProductAdapter(new ArrayList<>(), item -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("product_item", item); // Key: "product_item"
            try {
                androidx.navigation.Navigation.findNavController(view)
                        .navigate(R.id.action_categoryProductsFragment_to_productDetailFragment, bundle);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Lỗi navigation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        rvCategoryProducts.setAdapter(adapter);
        apiService = ApiClient.getApiService();
        loadProducts();
        view.findViewById(R.id.ivBack).setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }
    private void loadProducts() {
        if (categoryId == -1) return;
        apiService.getMenuItemsByCategory(categoryId).enqueue(new Callback<List<MenuItemDto>>() {
            @Override
            public void onResponse(Call<List<MenuItemDto>> call, Response<List<MenuItemDto>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<CategoryProductItem> uiList = new ArrayList<>();
                    for (MenuItemDto dto : response.body()) {
                        uiList.add(new CategoryProductItem(
                                dto.getItemId(),
                                dto.getImage(),
                                dto.getItemName(),
                                dto.getDescription(),
                                dto.getRating() != null ? dto.getRating() : 0.0,
                                dto.getPrice(),
                                dto.getDiscountPrice()
                        ));
                    }
                    adapter.setItems(uiList);
                } else {
                    Toast.makeText(getContext(), "Không có sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<MenuItemDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}