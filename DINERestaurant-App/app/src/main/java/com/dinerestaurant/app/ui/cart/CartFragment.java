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

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupListeners();

        loadCartData();
        loadDefaultAddress();

        // Nhận promotion từ PromotionsFragment
        getParentFragmentManager().setFragmentResultListener(
                "promotion_result",
                getViewLifecycleOwner(),
                (key, bundle) -> {
                    appliedPromotionId = bundle.getLong("promotionId");
                    applyPromotionPreview();
                }
        );
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
                }
        );

        rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartItems.setAdapter(adapter);
    }

    private void setupListeners() {
        viewRequire(R.id.llMyLocations)
                .setOnClickListener(v ->
                        Navigation.findNavController(v)
                                .navigate(R.id.action_cartFragment_to_myLocationsFragment));

        viewRequire(R.id.llPromotions)
                .setOnClickListener(v ->
                        Navigation.findNavController(v)
                                .navigate(R.id.action_cartFragment_to_promotionsFragment));

        viewRequire(R.id.llPaymentMethod)
                .setOnClickListener(v ->
                        Navigation.findNavController(v)
                                .navigate(R.id.action_cartFragment_to_paymentFragment));

        btnConfirm.setOnClickListener(v ->
                Toast.makeText(getContext(), "Place order", Toast.LENGTH_SHORT).show()
        );
    }

    // =====================================================
    // LOAD DATA
    // =====================================================
    private void loadCartData() {
        ApiClient.getCartApi().getCart().enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                if (!response.isSuccessful() || response.body() == null) return;

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

    private void loadDefaultAddress() {
        ApiClient.getUserAddressApi()
                .getDefaultAddress()
                .enqueue(new Callback<UserAddress>() {
                    @Override
                    public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserAddress address = response.body();
                            tvAddressTitle.setText("Delivery to  →  " + address.getLabel());
                            tvAddressDetail.setText(address.getAddressText());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAddress> call, Throwable t) {
                        tvAddressTitle.setText("Delivery to");
                        tvAddressDetail.setText("Select Your Location");
                    }
                });
    }

    // =====================================================
    // PROMOTION PREVIEW (KHÔNG GHI DB)
    // =====================================================
    private void applyPromotionPreview() {
        if (appliedPromotionId == null || cartId == null) return;

        ApplyPromotionRequest request =
                new ApplyPromotionRequest(cartId, appliedPromotionId.intValue());

        ApiClient.getPromotionApi()
                .applyPromotion(request)
                .enqueue(new Callback<ApplyPromotionResponse>() {
                    @Override
                    public void onResponse(
                            Call<ApplyPromotionResponse> call,
                            Response<ApplyPromotionResponse> response
                    ) {
                        if (!response.isSuccessful() || response.body() == null) return;

                        discountAmount =
                                response.body().getDiscountAmount().doubleValue();

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
