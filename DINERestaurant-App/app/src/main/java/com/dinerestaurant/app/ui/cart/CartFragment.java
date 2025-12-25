package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.TableSessionManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.dto.ApplyPromotionRequest;
import com.dinerestaurant.app.data.remote.dto.ApplyPromotionResponse;
import com.dinerestaurant.app.model.Cart;
import com.dinerestaurant.app.model.CartItem;
import com.dinerestaurant.app.model.UserAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    // ===== Views =====
    private RecyclerView rvCartItems;
    private CartAdapter adapter;

    private TextView tvSubtotalValue;
    private TextView tvDiscountValue;
    private TextView tvTotalValue;
    private Button btnConfirm;
    private Button btnCancel;

    private TextView tvAddressTitle, tvAddressDetail;

    // ===== State =====
    private Integer cartId;
    private Long appliedPromotionId = null;
    private double discountAmount = 0;
    private String selectedPaymentMethod = "Cash"; // Default
    private double totalAmount = 0;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        loadCartData();
        loadLocationFromSession(); // Đọc từ session trước
        loadDefaultAddress(); // Fallback nếu không có session

        // Nhận promotion từ PromotionsFragment
        getParentFragmentManager().setFragmentResultListener(
                "promotion_result",
                getViewLifecycleOwner(),
                (key, bundle) -> {
                    appliedPromotionId = bundle.getLong("promotionId");
                    applyPromotionPreview();
                });

        // Nhận payment method từ PaymentFragment
        NavController navController = Navigation.findNavController(view);
        navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selected_payment_method", "Cash")
                .observe(getViewLifecycleOwner(), method -> {
                    selectedPaymentMethod = (String) method;
                    // Update UI nếu cần
                    TextView tvPayment = view.findViewById(R.id.tvPaymentMethod);
                    if (tvPayment != null) {
                        tvPayment.setText(selectedPaymentMethod);
                    }
                });
    }

    // =====================================================
    // INIT
    // =====================================================
    private void initViews(View view) {
        rvCartItems = view.findViewById(R.id.rvCartItems);

        tvSubtotalValue = view.findViewById(R.id.tv_subtotal_value);
        tvDiscountValue = view.findViewById(R.id.tv_discount_value);
        tvTotalValue = view.findViewById(R.id.textView5);

        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnCancel = view.findViewById(R.id.btn_cancel);

        tvAddressTitle = view.findViewById(R.id.tv_address_id2);
        tvAddressDetail = view.findViewById(R.id.tv_address);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        }
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(
                new ArrayList<>(),
                requireContext().getAssets(),
                new CartAdapter.OnCartAction() {
                    @Override
                    public void onIncrease(int cartItemId, int qty) {
                        updateQuantity(cartItemId, qty + 1);
                    }

                    @Override
                    public void onDecrease(int cartItemId, int qty) {
                        if (qty > 1) {
                            updateQuantity(cartItemId, qty - 1);
                        } else {
                            deleteCartItem(cartItemId);
                        }
                    }

                    @Override
                    public void onDelete(int cartItemId) {
                        deleteCartItem(cartItemId);
                    }

                    @Override
                    public void onSelectionChanged() {
                        calculateAndRenderPrice();
                    }
                });

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(adapter);
    }

    private void setupListeners() {
        viewRequire(R.id.llMyLocations)
                .setOnClickListener(v -> Navigation.findNavController(v)
                        .navigate(R.id.action_cartFragment_to_myLocationsFragment));

        viewRequire(R.id.llPromotions)
                .setOnClickListener(v -> Navigation.findNavController(v)
                        .navigate(R.id.action_cartFragment_to_promotionsFragment));

        viewRequire(R.id.llPaymentMethod)
                .setOnClickListener(v -> Navigation.findNavController(v)
                        .navigate(R.id.action_cartFragment_to_paymentFragment));

        btnConfirm.setOnClickListener(v -> placeOrder());
    }

    /**
     * Xử lý đặt hàng
     */
    private void placeOrder() {
        if (adapter.getItems().isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đã chọn địa chỉ chưa
        TableSessionManager session = TableSessionManager.getInstance(requireContext());
        if (!session.hasOrderSelection()) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức nhận hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Nếu thanh toán bằng Cash -> chuyển thẳng đến Success
        if ("Cash".equalsIgnoreCase(selectedPaymentMethod)) {
            navigateToOrderSuccess();
        } else {
            // Thanh toán online -> hiển màn hình QR
            navigateToQRPayment();
        }
    }

    private void navigateToQRPayment() {
        try {
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putLong(QRPaymentFragment.ARG_AMOUNT, (long) totalAmount);
            bundle.putString(QRPaymentFragment.ARG_PAYMENT_METHOD, selectedPaymentMethod);

            navController.navigate(R.id.qrPaymentFragment, bundle);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToOrderSuccess() {
        try {
            TableSessionManager session = TableSessionManager.getInstance(requireContext());
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putLong(OrderSuccessFragment.ARG_AMOUNT, (long) totalAmount);
            bundle.putString(OrderSuccessFragment.ARG_PAYMENT_METHOD, selectedPaymentMethod);

            if (session.hasOrderSelection()) {
                bundle.putString(OrderSuccessFragment.ARG_ORDER_TYPE, session.getOrderType());
                bundle.putString("display_address", session.getDisplayAddress());
            }

            if (session.hasTableReservation()) {
                bundle.putString(OrderSuccessFragment.ARG_TABLE_ID, session.getTableId());
            }

            navController.navigate(R.id.orderSuccessFragment, bundle);

            // Clear session sau khi đặt hàng
            session.clearOrderSelection();
            session.clearTableReservation();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // =====================================================
    // LOAD DATA
    // =====================================================
    private void loadCartData() {
        ApiClient.getCartApi().getCart().enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (!response.isSuccessful() || response.body() == null)
                    return;

                Cart cart = response.body();
                cartId = cart.getCartId();

                adapter.updateData(cart.getCartItems());
                calculateAndRenderPrice();
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Load cart failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Đọc location đã chọn từ session (MyLocations)
     */
    private void loadLocationFromSession() {
        TableSessionManager session = TableSessionManager.getInstance(requireContext());

        if (session.hasOrderSelection()) {
            String orderType = session.getOrderType();
            String displayAddress = session.getDisplayAddress();

            // Hiển thị theo loại order
            switch (orderType) {
                case "dine_in":
                    tvAddressTitle.setText("Ăn tại quán");
                    tvAddressDetail.setText(displayAddress);
                    break;
                case "takeaway":
                    tvAddressTitle.setText("Mang về");
                    tvAddressDetail.setText(displayAddress);
                    break;
                case "delivery":
                    tvAddressTitle.setText("Giao tận nơi");
                    tvAddressDetail.setText(displayAddress);
                    break;
            }
        }
    }

    private void loadDefaultAddress() {
        // Nếu đã có selection từ session thì không cần load default
        TableSessionManager session = TableSessionManager.getInstance(requireContext());
        if (session.hasOrderSelection()) {
            return;
        }

        ApiClient.getUserAddressApi()
                .getDefaultAddress()
                .enqueue(new Callback<UserAddress>() {
                    @Override
                    public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserAddress address = response.body();
                            tvAddressTitle.setText("Giao tận nơi → " + address.getLabel());
                            tvAddressDetail.setText(address.getAddressText());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAddress> call, Throwable t) {
                        tvAddressTitle.setText("Chọn địa chỉ");
                        tvAddressDetail.setText("Nhấn để chọn phương thức nhận hàng");
                    }
                });
    }

    // =====================================================
    // PROMOTION PREVIEW (KHÔNG GHI DB)
    // =====================================================
    private void applyPromotionPreview() {
        if (appliedPromotionId == null || cartId == null)
            return;

        ApplyPromotionRequest request = new ApplyPromotionRequest(cartId, appliedPromotionId.intValue());

        ApiClient.getPromotionApi()
                .applyPromotion(request)
                .enqueue(new Callback<ApplyPromotionResponse>() {
                    @Override
                    public void onResponse(
                            Call<ApplyPromotionResponse> call,
                            Response<ApplyPromotionResponse> response) {
                        if (!response.isSuccessful() || response.body() == null)
                            return;

                        discountAmount = response.body().getDiscountAmount().doubleValue();

                        calculateAndRenderPrice();
                    }

                    @Override
                    public void onFailure(Call<ApplyPromotionResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Apply promotion failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // =====================================================
    // PRICE RENDER
    // =====================================================
    private void calculateAndRenderPrice() {
        double subtotal = 0;

        for (CartItem item : adapter.getItems()) {
            if (item.isSelected()) {
                subtotal += item.getMenuItem().getPrice() * item.getQuantity();
            }
        }

        // Subtotal
        tvSubtotalValue.setText(String.format("£ %.2f", subtotal));

        // Discount
        if (discountAmount > 0) {
            tvDiscountValue.setText(String.format("- £ %.2f", discountAmount));
        } else {
            tvDiscountValue.setText("__");
        }

        // Total
        double total = Math.max(0, subtotal - discountAmount);
        tvTotalValue.setText(String.format("£ %.2f", total));

        // Lưu total để dùng khi place order
        this.totalAmount = total;

        // Bottom bar price
        btnCancel.setText(String.format("£ %.2f", total));
    }

    // =====================================================
    // CART ACTIONS
    // =====================================================
    private void updateQuantity(int cartItemId, int qty) {
        Map<String, Object> body = new HashMap<>();
        body.put("cartItemId", cartItemId);
        body.put("quantity", qty);

        ApiClient.getCartApi().updateQuantity(body).enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                loadCartData();
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCartItem(int cartItemId) {
        ApiClient.getCartApi().removeItem(cartItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadCartData();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper
    private View viewRequire(int id) {
        return requireView().findViewById(id);
    }
}
