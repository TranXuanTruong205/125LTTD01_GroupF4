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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.model.Cart;
import com.dinerestaurant.app.model.CategoryProductItem;
import com.dinerestaurant.app.model.ItemOption;
import com.dinerestaurant.app.ui.cart.ItemOptionAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ProductDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        // 1. Nhận dữ liệu
        if (getArguments() != null) {
            currentMenuItem = (CategoryProductItem) getArguments().getSerializable("menu_item");
        }

        // 2. Ánh xạ View
        ImageView imgFood = view.findViewById(R.id.imgFood);
        TextView tvName = view.findViewById(R.id.tvFoodName);
        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvOriginalPrice = view.findViewById(R.id.tvProductOriginalPrice);
        TextView tvDiscountPrice = view.findViewById(R.id.tvProductDiscountPrice);
        TextView btnSeeAllReviews = view.findViewById(R.id.btnSeeAllReview);
        View btnBack = view.findViewById(R.id.btnBack);
        TextView tvQuantity = view.findViewById(R.id.tvQuantity);
        ImageButton btnDecrease = view.findViewById(R.id.btnDecrease);
        ImageButton btnIncrease = view.findViewById(R.id.btnIncrease);
        btnAddToBasket = view.findViewById(R.id.btnAddToBasket);

        // RecyclerView cho Options
        rvOptions = view.findViewById(R.id.rvOptions);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Hiển thị thông tin món
        if (currentMenuItem != null) {
            if (tvName != null) tvName.setText(currentMenuItem.getName());
            if (tvDescription != null) tvDescription.setText(currentMenuItem.getDescription());
            if (tvOriginalPrice != null && tvDiscountPrice != null) {
                double price = currentMenuItem.getPrice();
                Double discountPrice = currentMenuItem.getDiscountPrice();
                if (discountPrice != null && discountPrice > 0) {
                    tvOriginalPrice.setText(String.format("%,.0fđ", price));
                    tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG); // Gạch ngang
                    tvDiscountPrice.setText(String.format(" %,.0fđ", discountPrice));
                } else {
                    tvOriginalPrice.setVisibility(View.GONE);
                    tvDiscountPrice.setText(String.format("%,.0fđ", price));
                }
            }
            // --- LOAD ẢNH TỪ ASSETS ---
            if (imgFood != null && currentMenuItem.getImagePath() != null) {
                try {
                    java.io.InputStream is = requireContext().getAssets().open(currentMenuItem.getImagePath());
                    android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(is);
                    imgFood.setImageBitmap(bitmap);
                    is.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    imgFood.setImageResource(R.drawable.ic_launcher_background); // Ảnh default nếu lỗi
                }
            }
            // --------------------------

            // Cập nhật giá ban đầu lên nút Add
            updateTotalPrice();

            // GỌI API LẤY OPTIONS
            loadOptions(currentMenuItem.getItemId());
        }

        // --- CÁC SỰ KIỆN CLICK ---

        // Xem Review
        if (btnSeeAllReviews != null) {
            btnSeeAllReviews.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("itemId", currentMenuItem.getItemId());

                Navigation.findNavController(view)
                        .navigate(R.id.action_productDetailFragment_to_reviewListFragment, bundle);
            });

        }

        // Quay lại
        if (btnBack != null) btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Tăng giảm số lượng
        if (btnDecrease != null) {
            btnDecrease.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQuantity.setText(String.valueOf(quantity));
                    updateTotalPrice(); // Cập nhật lại giá
                }
            });
        }

        if (btnIncrease != null) {
            btnIncrease.setOnClickListener(v -> {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice(); // Cập nhật lại giá
            });
        }

        // Thêm vào giỏ
        setupAddToBasketButton();

        return view;
    }

    private void loadOptions(int itemId) {
        ApiClient.getApiService().getMenuOptions(itemId).enqueue(new Callback<List<ItemOption>>() {
            @Override
            public void onResponse(Call<List<ItemOption>> call, Response<List<ItemOption>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    optionList = response.body();

                    // Khởi tạo Adapter
                    optionAdapter = new ItemOptionAdapter(optionList, () -> {
                        // Khi user tick chọn option -> Cập nhật lại tổng tiền
                        updateTotalPrice();
                    });

                    rvOptions.setAdapter(optionAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ItemOption>> call, Throwable t) {
                // Có thể log lỗi
            }
        });
    }

    private void updateTotalPrice() {
        if (currentMenuItem == null || btnAddToBasket == null) return;

        double basePrice = currentMenuItem.getDisplayPrice();
        double optionsPrice = 0;

        // Cộng tiền các option đã chọn
        if (optionList != null) {
            for (ItemOption opt : optionList) {
                if (opt.isSelected()) {
                    optionsPrice += opt.getExtraPrice();
                }
            }
        }

        double total = (basePrice + optionsPrice) * quantity;

        // Format tiền tệ đơn giản
        btnAddToBasket.setText(String.format("Add to Basket - %.0fđ", total));
    }

    private void setupAddToBasketButton() {
        if (btnAddToBasket != null) {
            btnAddToBasket.setOnClickListener(v -> {
                if (currentMenuItem == null) return;

                Map<String, Object> body = new HashMap<>();
                body.put("menuItemId", currentMenuItem.getItemId());
                body.put("quantity", quantity);

                // Gửi kèm danh sách optionId đã chọn
                List<Integer> selectedOptionIds = new ArrayList<>();
                if (optionList != null) {
                    for (ItemOption opt : optionList) {
                        if (opt.isSelected()) {
                            selectedOptionIds.add(opt.getOptionId());
                        }
                    }
                }
                body.put("optionIds", selectedOptionIds);

                ApiClient.getCartApi().addToCart(body).enqueue(new Callback<Cart>() {
                    @Override
                    public void onResponse(Call<Cart> call, Response<Cart> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã thêm vào giỏ!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Lỗi thêm giỏ hàng", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Cart> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }
}