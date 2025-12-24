package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.model.Cart;
import com.dinerestaurant.app.model.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    private RecyclerView rvCartItems;
    private CartAdapter adapter;
    private TextView tvSubtotalValue, tvTotalValue;
    private Button btnPlaceOrder;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        loadCartData();
    }

    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rvCartItems);
        tvSubtotalValue = view.findViewById(R.id.tv_subtotal_value);
        tvTotalValue = view.findViewById(R.id.textView5); /// Total ở dưới cùng

        // Setup nút Back
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().onBackPressed();
            });
        }
    }

    private void setupRecyclerView() {
        // Khởi tạo Adapter với 3 hành động: Tăng, Giảm, Xóa
        adapter = new CartAdapter(new ArrayList<>(),requireContext().getAssets(), new CartAdapter.OnCartAction() {
            @Override
            public void onIncrease(int cartItemId, int currentQty) {
                updateCartItemQuantity(cartItemId, currentQty + 1);
            }

            @Override
            public void onDecrease(int cartItemId, int currentQty) {
                if (currentQty > 1) {
                    updateCartItemQuantity(cartItemId, currentQty - 1);
                } else {
                    deleteCartItem(cartItemId); // Giảm về 0 thì xóa
                }
            }

            @Override
            public void onDelete(int cartItemId){
                deleteCartItem(cartItemId);
            }
            @Override
            public void onSelectionChanged() {
                calculateAndDisplayTotal(); // <--- Thêm đoạn này vào trong listener
            }
        });

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(adapter);
    }

    private void loadCartData() {
        ApiClient.getCartApi().getCart().enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Cart cart = response.body();

                    // Cập nhật danh sách món ăn
                    if (cart.getCartItems() != null) {
                        adapter.updateData(cart.getCartItems());
                        calculateAndDisplayTotal();
                    } else {
                        adapter.updateData(new ArrayList<>());
                    }

                    // Cập nhật tổng tiền
                    updatePriceUI(cart.getTotalAmount());
                } else {
                    // Nếu lỗi (ví dụ chưa đăng nhập hoặc giỏ rỗng)
                    adapter.updateData(new ArrayList<>());
                    updatePriceUI(0);
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartItemQuantity(int cartItemId, int newQty) {
        Map<String, Object> body = new HashMap<>();
        body.put("cartItemId", cartItemId);
        body.put("quantity", newQty);

        ApiClient.getCartApi().updateQuantity(body).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (response.isSuccessful()) {
                    loadCartData(); // Cập nhật lại toàn bộ giỏ
                } else {
                    Toast.makeText(getContext(), "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCartItem(int cartItemId) {
        ApiClient.getCartApi().removeItem(cartItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa món", Toast.LENGTH_SHORT).show();
                    loadCartData();
                } else {
                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePriceUI(double subtotal) {
        // Cập nhật giao diện tiền
        if (tvSubtotalValue != null) tvSubtotalValue.setText(String.format("£ %.2f", subtotal));
        if (tvTotalValue != null) tvTotalValue.setText(String.format("£ %.2f", subtotal));
    }
    // 2. Viết hàm tính tổng tiền dựa trên các CheckBox đã chọn
    private void calculateAndDisplayTotal() {
        double total = 0;
        // Giả sử adapter.getItems() trả về danh sách CartItem hiện tại
        for (CartItem item : adapter.getItems()) {
            if (item.isSelected()) {
                // Cộng (giá món ăn + giá topping nếu có) * số lượng
                // Ở giai đoạn này nếu chưa có hàm getLinePrice ở model Android,
                // bạn có thể tạm tính đơn giản hoặc dùng dữ liệu từ BE.
                total += (item.getMenuItem().getPrice() * item.getQuantity());
            }
        }
        updatePriceUI(total);
    }
}