package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;

public class ProductDetailFragment extends Fragment {

    private int quantity = 1;
    public ProductDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 1. Inflate view ra biến để xử lý
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // 2. Ánh xạ

        TextView btnSeeAllReviews = view.findViewById(R.id.btnSeeAllReview);
        View btnBack = view.findViewById(R.id.btnBack); // Giả sử bạn đặt ID là btnBack hoặc ivBack
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);
        Button btnAddToBasket = view.findViewById(R.id.btnAddToBasket);

        // --- SỰ KIỆN CLICK "See all review" ---
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

        // --- SỰ KIỆN CLICK NÚT BACK (Quay lại trang trước) ---
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                requireActivity().onBackPressed();
            });
        }

        //Tăng giảm số lượng

        // Nút Trừ (-)
        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) { // Chỉ trừ khi số lượng > 1
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                }
            });
        }

        // Nút Cộng (+)
        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            });
        }

        // --- LOGIC ADD TO BASKET (Dùng Toast) ---
        if (btnAddToBasket != null) {
            btnAddToBasket.setOnClickListener(v -> {
                // Hiển thị thông báo Toast
                String message = "Đã thêm " + quantity + " sản phẩm vào giỏ hàng!";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            });
        }

        return view;
    }
}
