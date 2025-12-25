package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
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
    private List<CategoryItem> categoryItems = new ArrayList<>();
    public CategoryFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        apiService = ApiClient.getApiService();
        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));
        // Không cần truyền AssetManager nữa
        adapter = new CategoryAdapter(categoryItems, item -> {
            Bundle args = new Bundle();
            args.putInt("categoryId", item.getId());
            args.putString("categoryName", item.getName());

            androidx.navigation.Navigation.findNavController(requireView())
                    .navigate(R.id.action_categoryFragment_to_categoryProductsFragment, args);
        });
        rvCategories.setAdapter(adapter);
        loadCategories();
        view.findViewById(R.id.ivBack).setOnClickListener(v -> requireActivity().onBackPressed());
        return view;
    }
    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }
                List<CategoryItem> apiList = new ArrayList<>();
                for (CategoryDto dto : response.body()) {
                    // LẤY TRỰC TIẾP TỪ DB, KHÔNG QUA HÀM MAP SWITCH-CASE
                    // DB cần lưu string: "images/categories/ic_burger.png"
                    // Glide sẽ tự xử lý phần extension.
                    apiList.add(new CategoryItem(
                            dto.getCategoryId(),
                            dto.getCategoryName(),
                            dto.getIcon() // Lấy trực tiếp icon từ DB
                    ));
                }

                adapter.setItems(apiList);
            }
            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}