package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
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

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.model.Cart;
import com.dinerestaurant.app.model.CategoryProductItem;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {

    private int quantity = 1;
    private CategoryProductItem currentMenuItem;

    public ProductDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // 1. Nhận dữ liệu từ màn hình trước
        if (getArguments() != null) {
            currentMenuItem = (CategoryProductItem) getArguments().getSerializable("menu_item");
        }

        // 2. Ánh xạ View
        ImageView imgFood = view.findViewById(R.id.imgFood);
        TextView tvFoodName = view.findViewById(R.id.tvFoodName);
        TextView tvPrice = view.findViewById(R.id.tvDescription); // Lưu ý: XML của bạn đang dùng ID này cho mô tả? Hãy kiểm tra lại bên dưới
        // Chỉnh sửa lại ánh xạ cho đúng với XML của bạn:
        // Trong XML bạn có: tvFoodName, tvDescription.
        // Phần giá bạn đang dùng TextView không ID trong LinearLayout.
        // Để đơn giản, ta sẽ focus vào Tên và Mô tả trước.

        // Ánh xạ lại chính xác theo XML:
        TextView tvName = view.findViewById(R.id.tvFoodName);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

        // Nút bấm
        TextView btnSeeAllReviews = view.findViewById(R.id.btnSeeAllReview);
        View btnBack = view.findViewById(R.id.btnBack);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);
        Button btnAddToBasket = view.findViewById(R.id.btnAddToBasket);

        // 3. Hiển thị dữ liệu lên giao diện
        if (currentMenuItem != null) {
            if (tvName != null) tvName.setText(currentMenuItem.getName());
//            if (tvDescription != null) tvDescription.setText(currentMenuItem.getDescription())
        }

        // --- CÁC SỰ KIỆN CLICK (Giữ nguyên logic của bạn) ---
        // Xem Review
        if (btnSeeAllReviews != null) {
            btnSeeAllReviews.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(view)
                            .navigate(R.id.action_productDetailFragment_to_reviewListFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }

        // Tăng giảm số lượng
        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                }
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            });
        }

        // Nút Thêm vào giỏ
        if (btnAddToBasket != null) {
            btnAddToBasket.setOnClickListener(v -> {
                if (currentMenuItem == null) {
                    Toast.makeText(getContext(), "Lỗi: Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> body = new HashMap<>();
                body.put("menuItemId", currentMenuItem.getItemId());
                body.put("quantity", quantity);

                ApiClient.getCartApi().addToCart(body).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã thêm " + quantity + " món vào giỏ!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Thử in lỗi chi tiết nếu có
                            Toast.makeText(getContext(), "Lỗi thêm giỏ: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        return view;
    }
}