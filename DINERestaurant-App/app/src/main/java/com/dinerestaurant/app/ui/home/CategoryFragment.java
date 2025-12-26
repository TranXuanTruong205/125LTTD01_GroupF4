package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiService;
import com.dinerestaurant.app.data.remote.dto.CategoryDto;
import com.dinerestaurant.app.model.CategoryItem;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class CategoryFragment extends Fragment {
    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private ApiService apiService;
    public CategoryFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        adapter = new CategoryAdapter(new ArrayList<>(), requireContext().getAssets(), item -> {
            Bundle args = new Bundle();
            args.putInt("categoryId", item.getId());
            args.putString("categoryName", item.getName());

            // --- THÊM DÒNG NÀY ---
            args.putString("categoryIcon", item.getImagePath());
            // ---------------------

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_categoryFragment_to_categoryProductsFragment, args);
        });
        rvCategories.setAdapter(adapter);
        view.findViewById(R.id.ivBack).setOnClickListener(v -> requireActivity().onBackPressed());
        apiService = ApiClient.getApiService();
        loadCategories();
        return view;
    }
    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryItem> items = new ArrayList<>();
                    for (CategoryDto dto : response.body()) {
                        // Lấy ảnh từ DB, nếu null dùng ảnh mặc định
                        String iconPath = dto.getIcon();
                        if (iconPath == null || iconPath.isEmpty()) {
                            iconPath = "images/categories/ic_more.png";
                        }

                        items.add(new CategoryItem(
                                dto.getCategoryId(), // ID thật từ DB
                                dto.getCategoryName(),
                                iconPath
                        ));
                    }
                    adapter.setItems(items);
                } else {
                    Toast.makeText(getContext(), "Không tải được danh mục", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
