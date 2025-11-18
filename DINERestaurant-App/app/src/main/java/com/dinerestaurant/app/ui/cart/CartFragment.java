package com.dinerestaurant.app.ui.cart;

import android.app.AlertDialog;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;

import java.text.DecimalFormat;

public class CartFragment extends Fragment {

    // Header
    private ImageButton btnBack;
    private TextView tvTitle;

    // Food Items - Card 1 (Chicken Burger)
    private TextView tvFoodName1, tvOriginalPrice1, tvDiscountPrice1, tvQuantity1;
    private ImageButton btnEdit1, btnDelete1, btnMinus1, btnPlus1;

    // Food Items - Card 2 (Ramen Noodles)
    private TextView tvFoodName2, tvOriginalPrice2, tvDiscountPrice2, tvQuantity2;
    private ImageButton btnEdit2, btnDelete2, btnMinus2, btnPlus2;

    // Food Items - Card 3 (Cherry Tomato Salad)
    private TextView tvFoodName3, tvDiscountPrice3, tvQuantity3;
    private ImageButton btnEdit3, btnDelete3, btnMinus3, btnPlus3;

    // Delivery, Payment, Promotions
    private TextView tvAddress, tvCash, tvPromotions;
    private ImageButton btnEditAddress, btnEditPayment, btnEditPromotions;

    // Price Summary
    private TextView tvSubtotalValue, tvDeliveryValue, tvDiscountValue, tvTotalValue, tvTotalBottom;

    // Bottom Buttons
    private Button btnTotalPrice, btnPlaceOrder;

    // Data variables
    private int quantity1 = 1, quantity2 = 1, quantity3 = 1;
    private double price1 = 6.00, price2 = 15.00, price3 = 8.00;
    private double originalPrice1 = 10.00, originalPrice2 = 10.00;
    private double deliveryFee = 0.0;
    private double discount = 0.0;
    private String selectedAddress = "Select Your Location";
    private String selectedPayment = "Select Payment Method";

    private DecimalFormat df = new DecimalFormat("#.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        initViews(view);
        setupListeners();
        observeResults();
        updatePrices();

        return view;
    }

    private void initViews(View view) {
        // Header
        btnBack = view.findViewById(R.id.imageButton3);
        tvTitle = view.findViewById(R.id.textView2);

        // Card 1 - Chicken Burger
        tvFoodName1 = view.findViewById(R.id.tv_order_id2);
        tvOriginalPrice1 = view.findViewById(R.id.tv_price2);
        tvDiscountPrice1 = view.findViewById(R.id.tv_price8);
        tvQuantity1 = view.findViewById(R.id.textView6);
        btnEdit1 = view.findViewById(R.id.imageButton2);
        btnDelete1 = view.findViewById(R.id.imageButton5);
        btnMinus1 = view.findViewById(R.id.imageButton15);
        btnPlus1 = view.findViewById(R.id.imageButton16);

        // Card 2 - Ramen Noodles
        tvFoodName2 = view.findViewById(R.id.tv_order_id6);
        tvOriginalPrice2 = view.findViewById(R.id.tv_price4);
        tvDiscountPrice2 = view.findViewById(R.id.tv_price5);
        tvQuantity2 = view.findViewById(R.id.textView19);
        btnEdit2 = view.findViewById(R.id.imageButton6);
        btnDelete2 = view.findViewById(R.id.imageButton7);
        btnMinus2 = view.findViewById(R.id.imageButton18);
        btnPlus2 = view.findViewById(R.id.imageButton20);

        // Card 3 - Cherry Tomato Salad
        tvFoodName3 = view.findViewById(R.id.tv_order_id7);
        tvDiscountPrice3 = view.findViewById(R.id.tv_price9);
        tvQuantity3 = view.findViewById(R.id.textView22);
        btnEdit3 = view.findViewById(R.id.imageButton8);
        btnDelete3 = view.findViewById(R.id.imageButton11);
        btnMinus3 = view.findViewById(R.id.imageButton21);
        btnPlus3 = view.findViewById(R.id.imageButton23);

        // Delivery, Payment, Promotions
        tvAddress = view.findViewById(R.id.tv_address);
        tvCash = view.findViewById(R.id.tv_cash);
        btnEditAddress = view.findViewById(R.id.imageButton12);
        btnEditPayment = view.findViewById(R.id.imageButton13);
        btnEditPromotions = view.findViewById(R.id.imageButton14);

        // Price Summary
        tvSubtotalValue = view.findViewById(R.id.tv_subtotal_value);
        tvDeliveryValue = view.findViewById(R.id.tv_delivery_value);
        tvDiscountValue = view.findViewById(R.id.tv_discount_value);
        tvTotalValue = view.findViewById(R.id.textView5);

        // Bottom
        btnTotalPrice = view.findViewById(R.id.btn_cancel);
        btnPlaceOrder = view.findViewById(R.id.btn_confirm);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        // === CARD 1 - Chicken Burger ===
        btnMinus1.setOnClickListener(v -> {
            if (quantity1 > 1) {
                quantity1--;
                tvQuantity1.setText(String.valueOf(quantity1));
                updatePrices();
            }
        });

        btnPlus1.setOnClickListener(v -> {
            quantity1++;
            tvQuantity1.setText(String.valueOf(quantity1));
            updatePrices();
        });

        btnEdit1.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit Chicken Burger", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to edit screen
        });

        btnDelete1.setOnClickListener(v -> {
            showDeleteDialog("Chicken Burger", 1);
        });

        // === CARD 2 - Ramen Noodles ===
        btnMinus2.setOnClickListener(v -> {
            if (quantity2 > 1) {
                quantity2--;
                tvQuantity2.setText(String.valueOf(quantity2));
                updatePrices();
            }
        });

        btnPlus2.setOnClickListener(v -> {
            quantity2++;
            tvQuantity2.setText(String.valueOf(quantity2));
            updatePrices();
        });

        btnEdit2.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit Ramen Noodles", Toast.LENGTH_SHORT).show();
        });

        btnDelete2.setOnClickListener(v -> {
            showDeleteDialog("Ramen Noodles", 2);
        });

        // === CARD 3 - Cherry Tomato Salad ===
        btnMinus3.setOnClickListener(v -> {
            if (quantity3 > 1) {
                quantity3--;
                tvQuantity3.setText(String.valueOf(quantity3));
                updatePrices();
            }
        });

        btnPlus3.setOnClickListener(v -> {
            quantity3++;
            tvQuantity3.setText(String.valueOf(quantity3));
            updatePrices();
        });

        btnEdit3.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Edit Cherry Tomato Salad", Toast.LENGTH_SHORT).show();
        });

        btnDelete3.setOnClickListener(v -> {
            showDeleteDialog("Cherry Tomato Salad", 3);
        });

        // === DELIVERY ADDRESS ===
        btnEditAddress.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Select delivery address", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to address selection
            // Tạm thời set địa chỉ mẫu
            selectedAddress = "221B Baker Street, London, UK";
            tvAddress.setText(selectedAddress);
        });

        // === PAYMENT METHOD ===
        btnEditPayment.setOnClickListener(v -> {
            // Navigate to PaymentFragment
            NavController navController = Navigation.findNavController(v);
            // navController.navigate(R.id.action_cartFragment_to_paymentFragment);
            Toast.makeText(requireContext(), "Opening payment methods...", Toast.LENGTH_SHORT).show();
        });

        // === PROMOTIONS ===
        btnEditPromotions.setOnClickListener(v -> {
            // Navigate to PromotionsFragment
            NavController navController = Navigation.findNavController(v);
            // navController.navigate(R.id.action_cartFragment_to_promotionsFragment);
            Toast.makeText(requireContext(), "Opening promotions...", Toast.LENGTH_SHORT).show();
        });

        // === PLACE ORDER BUTTON ===
        btnPlaceOrder.setOnClickListener(v -> {
            if (selectedAddress.equals("Select Your Location")) {
                Toast.makeText(requireContext(), "Please select delivery address", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPayment.equals("Select Payment Method")) {
                Toast.makeText(requireContext(), "Please select payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            showPlaceOrderDialog();
        });
    }

    private void observeResults() {
        // Nhận kết quả từ PaymentFragment
        NavController navController = Navigation.findNavController(requireView());
        navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selected_payment_method", "")
                .observe(getViewLifecycleOwner(), paymentMethod -> {
                    if (!paymentMethod.isEmpty()) {
                        selectedPayment = paymentMethod;
                        tvCash.setText(paymentMethod);
                        Toast.makeText(requireContext(),
                                "Payment method updated: " + paymentMethod,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // TODO: Nhận kết quả từ PromotionsFragment
        navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selected_discount", 0.0)
                .observe(getViewLifecycleOwner(), discountValue -> {
                    if (discountValue > 0) {
                        discount = discountValue;
                        updatePrices();
                    }
                });
    }

    private void updatePrices() {
        // Tính subtotal
        double subtotal = (price1 * quantity1) + (price2 * quantity2) + (price3 * quantity3);

        // Tính total
        double total = subtotal + deliveryFee - discount;

        // Update UI
        tvSubtotalValue.setText("£ " + df.format(subtotal));

        if (deliveryFee == 0) {
            tvDeliveryValue.setText("FREE");
            tvDeliveryValue.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvDeliveryValue.setText("£ " + df.format(deliveryFee));
            tvDeliveryValue.setTextColor(getResources().getColor(android.R.color.black));
        }

        if (discount > 0) {
            tvDiscountValue.setText("- £ " + df.format(discount));
            tvDiscountValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvDiscountValue.setText("__");
        }

        tvTotalValue.setText("£ " + df.format(total));
        btnTotalPrice.setText("£ " + df.format(total));
    }

    private void showDeleteDialog(String itemName, int itemNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Remove Item")
                .setMessage("Are you sure you want to remove " + itemName + " from cart?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    // Xóa item
                    switch (itemNumber) {
                        case 1:
                            quantity1 = 0;
                            price1 = 0;
                            // Ẩn card hoặc remove khỏi UI
                            Toast.makeText(requireContext(), itemName + " removed", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            quantity2 = 0;
                            price2 = 0;
                            Toast.makeText(requireContext(), itemName + " removed", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            quantity3 = 0;
                            price3 = 0;
                            Toast.makeText(requireContext(), itemName + " removed", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    updatePrices();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void showPlaceOrderDialog() {
        double total = (price1 * quantity1) + (price2 * quantity2) + (price3 * quantity3)
                + deliveryFee - discount;

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Order")
                .setMessage("Total: £" + df.format(total) + "\n\n" +
                        "Delivery to: " + selectedAddress + "\n" +
                        "Payment: " + selectedPayment + "\n\n" +
                        "Place this order?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    // Xử lý đặt hàng
                    Toast.makeText(requireContext(),
                            "Order placed successfully!",
                            Toast.LENGTH_LONG).show();

                    // Navigate to OrderDetailFragment hoặc Order Success screen
                    NavController navController = Navigation.findNavController(requireView());
                    // navController.navigate(R.id.action_cartFragment_to_orderDetailFragment);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}