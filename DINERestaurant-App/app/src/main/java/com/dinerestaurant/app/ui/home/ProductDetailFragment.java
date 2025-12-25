package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiService;
import com.dinerestaurant.app.model.CategoryProductItem;
import com.dinerestaurant.app.model.ItemOption;
import com.dinerestaurant.app.ui.cart.ItemOptionAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ProductDetailFragment extends Fragment {
    private int quantity = 1;
    private CategoryProductItem currentMenuItem;
    private RecyclerView rvOptions;
    private ItemOptionAdapter optionAdapter;
    private List<ItemOption> optionList = new ArrayList<>();
    private Button btnAddToBasket;

    // View mapping variables
    private ImageView imgFood;
    private TextView tvName, tvDescription, tvQuantity; // Added tvPrice logic dynamically if needed or rely on button text
    public ProductDetailFragment() { }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        // 1. Ánh xạ View (Mapping)
        imgFood = view.findViewById(R.id.imgFood); // Đảm bảo ID này đúng với layout XML của bạn
        tvName = view.findViewById(R.id.tvFoodName);
        tvDescription = view.findViewById(R.id.tvDescription);

        TextView btnSeeAllReviews = view.findViewById(R.id.btnSeeAllReview);
        View btnBack = view.findViewById(R.id.btnBack);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToBasket = view.findViewById(R.id.btnAddToBasket);
        rvOptions = view.findViewById(R.id.rvOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        // 2. Nhận dữ liệu từ Bundle
        if (getArguments() != null) {
            // Key phải khớp với key bên CategoryProductsFragment gửi sang ("product_item")
            if (getArguments().containsKey("product_item")) {
                currentMenuItem = (CategoryProductItem) getArguments().getSerializable("product_item");
            } else if (getArguments().containsKey("menu_item")) {
                // Fallback nếu bên kia gửi key cũ
                currentMenuItem = (CategoryProductItem) getArguments().getSerializable("menu_item");
            }
        }
        // 3. Hiển thị dữ liệu lên giao diện
        if (currentMenuItem != null) {
            // Tên & Mô tả
            if (tvName != null) tvName.setText(currentMenuItem.getName());
            if (tvDescription != null) tvDescription.setText(currentMenuItem.getDescription());

            // Cập nhật giá lên nút Add
            updateTotalPrice();
            // --- LOAD ẢNH VỚI GLIDE ---
            if (imgFood != null) {
                String imagePath = currentMenuItem.getImagePath();
                Object imageSource;
                if (imagePath != null && !imagePath.startsWith("http")) {
                    imageSource = "file:///android_asset/" + imagePath;
                } else {
                    imageSource = imagePath;
                }
                Glide.with(this)
                        .load(imageSource)
                        .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh chờ
                        .error(android.R.drawable.ic_menu_gallery)       // Ảnh lỗi
                        .into(imgFood);
            }
            // Gọi API lấy danh sách Topping/Options
            loadOptions(currentMenuItem.getItemId());
        }
        // 4. Sự kiện click nút bấm
        setupEventHandlers(btnBack, btnSeeAllReviews, btnDecrease, btnIncrease, btnAddToBasket, view);
        return view;
    }

    private void setupEventHandlers(View btnBack, View btnSeeAllReviews, View btnDecrease, View btnIncrease, View btnAddToBasket, View rootView) {
        // Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
        // See reviews
        if (btnSeeAllReviews != null) {
            btnSeeAllReviews.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(rootView).navigate(R.id.action_productDetailFragment_to_reviewListFragment);
                } catch (Exception e) { e.printStackTrace(); }
            });
        }
        // Tăng/Giảm số lượng
        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice();
                }
            });
        }
        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            });
        }
        // Add to Basket
        if (btnAddToBasket != null) {
            btnAddToBasket.setOnClickListener(v -> {
                // Logic thêm vào giỏ hàng (Hiện tại Toast, sau này gọi API)
                String msg = "Đã thêm " + quantity + " " + (currentMenuItem != null ? currentMenuItem.getName() : "món") + " vào giỏ!";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void loadOptions(int itemId) {
        // Gọi API lấy option cho món ăn
        // (Giả sử bạn đã có phương thức getOptionsByItem trong ApiService hoặc tương tự)
        // Hiện tại để trống hoặc demo
        // ApiClient.getApiService().getItemOptions(itemId)...
    }
    private void updateTotalPrice() {
        if (currentMenuItem == null || btnAddToBasket == null) return;

        double basePrice = currentMenuItem.getDiscountPrice() != null ?
                currentMenuItem.getDiscountPrice() : currentMenuItem.getPrice();
        double total = basePrice * quantity;

        // Cộng thêm giá option nếu có (logic sau này phát triển thêm)
        /*
        for (ItemOption opt : optionList) {
             if (opt.isSelected()) total += opt.getExtraPrice();
        }
        */
        btnAddToBasket.setText("Add to Basket - " + String.format("%,.0fđ", total));
    }
}