package com.dinerestaurant.app.ui.orders;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;

public class OrderDetailFragment extends Fragment {

    // Header views
    private ImageButton btnBack;
    private ImageButton btnMore;
    private TextView tvOrderId;

    // Stepper views
    private CardView step1, step2, step3, step4;
    private View line1, line2, line3;

    // Food item views
    private TextView tvOrderId0, tvPrice2, tvPrice8;
    private TextView tvOrderId3, tvPrice4, tvPrice5;
    private TextView tvOrderIdItem3, tvPrice7, tvPrice9;

    // Delivery & Payment views
    private TextView tvAddress;
    private TextView tvCash;

    // Total views
    private TextView tvSubtotalValue;
    private TextView tvDeliveryValue;
    private TextView tvDiscountValue;

    // Bottom buttons
    private Button btnCancel;
    private Button btnConfirm;

    // Data
    private String orderId = "SP 0023900";
    private int currentStep = 2; // Bước hiện tại (1-4)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        initViews(view);
        // LOẠI BỎ: setupData();, setupListeners();, updateStepper();
        // Sẽ được gọi trong onViewCreated

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // GỌI CÁC PHƯƠNG THỨC TƯƠNG TÁC VỚI VIEW TẠI ĐÂY ĐỂ TRÁNH LỖI IllegalStateException
        setupData();
        setupListeners();
        updateStepper();
    }

    private void initViews(View view) {
        // Header
        btnBack = view.findViewById(R.id.imageButton3);
        btnMore = view.findViewById(R.id.imageButton);
        tvOrderId = view.findViewById(R.id.textView2);

        // Stepper
        step1 = view.findViewById(R.id.step1);
        step2 = view.findViewById(R.id.step2);
        step3 = view.findViewById(R.id.step3);
        step4 = view.findViewById(R.id.step4);
        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);

        // Food items
        tvOrderId0 = view.findViewById(R.id.tv_order_id0);
        tvPrice2 = view.findViewById(R.id.tv_price2);
        tvPrice8 = view.findViewById(R.id.tv_price8);

        tvOrderId3 = view.findViewById(R.id.tv_order_id3);
        tvPrice4 = view.findViewById(R.id.tv_price4);
        tvPrice5 = view.findViewById(R.id.tv_price5);

        tvOrderIdItem3 = view.findViewById(R.id.tv_order_id);
        tvPrice7 = view.findViewById(R.id.tv_price7);
        tvPrice9 = view.findViewById(R.id.tv_price9);

        // Delivery & Payment
        tvAddress = view.findViewById(R.id.tv_address);
        tvCash = view.findViewById(R.id.tv_cash);

        // Totals
        tvSubtotalValue = view.findViewById(R.id.tv_subtotal_value);
        tvDeliveryValue = view.findViewById(R.id.tv_delivery_value);
        tvDiscountValue = view.findViewById(R.id.tv_discount_value);

        // Bottom buttons
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);
    }

    private void setupData() {
        // Lấy dữ liệu từ arguments nếu có
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id", "SP 0023900");
            currentStep = getArguments().getInt("current_step", 2);
        }

        // Set order ID
        tvOrderId.setText(orderId);

        // Set payment method từ PaymentFragment nếu có
        // Lệnh gọi requireView() ở đây giờ đã an toàn vì nó nằm trong onViewCreated()
        NavController navController = Navigation.findNavController(requireView());
        navController.getCurrentBackStackEntry()
                .getSavedStateHandle()
                .getLiveData("selected_payment_method", "")
                .observe(getViewLifecycleOwner(), paymentMethod -> {
                    if (!paymentMethod.isEmpty()) {
                        tvCash.setText(paymentMethod);
                    }
                });
    }

    private void setupListeners() {
        // Nút Back
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        // Nút More (menu options)
        btnMore.setOnClickListener(v -> {
            showMoreOptions();
        });

        // Click vào card Payment để thay đổi phương thức thanh toán
        // Lệnh gọi requireView() ở đây giờ đã an toàn
        View paymentCard = (View) requireView().findViewById(R.id.tv_payment_method2).getParent().getParent();
        paymentCard.setOnClickListener(v -> {
            // Navigate đến PaymentFragment
            NavController navController = Navigation.findNavController(v);
            // navController.navigate(R.id.action_orderDetailFragment_to_paymentFragment);
            Toast.makeText(requireContext(), "Navigate to Payment", Toast.LENGTH_SHORT).show();
        });

        // Nút Cancel Order
        btnCancel.setOnClickListener(v -> {
            showCancelOrderDialog();
        });

        // Nút Confirm
        btnConfirm.setOnClickListener(v -> {
            confirmOrder();
        });
    }

    private void updateStepper() {
        // Reset tất cả về màu xám
        resetStepperColors();

        // Update theo currentStep
        switch (currentStep) {
            case 1:
                updateStepColor(step1, line1, true);
                break;
            case 2:
                updateStepColor(step1, line1, true);
                updateStepColor(step2, line2, true);
                break;
            case 3:
                updateStepColor(step1, line1, true);
                updateStepColor(step2, line2, true);
                updateStepColor(step3, line3, true);
                break;
            case 4:
                updateStepColor(step1, line1, true);
                updateStepColor(step2, line2, true);
                updateStepColor(step3, line3, true);
                updateStepColor(step4, null, true);
                break;
        }
    }

    private void resetStepperColors() {
        int grayColor = getResources().getColor(android.R.color.darker_gray);
        step1.setCardBackgroundColor(grayColor);
        step2.setCardBackgroundColor(grayColor);
        step3.setCardBackgroundColor(grayColor);
        step4.setCardBackgroundColor(grayColor);
        line1.setBackgroundColor(grayColor);
        line2.setBackgroundColor(grayColor);
        line3.setBackgroundColor(grayColor);
    }

    private void updateStepColor(CardView step, View line, boolean isActive) {
        int activeColor = getResources().getColor(android.R.color.holo_orange_dark); // #FF6B35
        if (isActive) {
            step.setCardBackgroundColor(activeColor);
            if (line != null) {
                line.setBackgroundColor(activeColor);
            }
        }
    }

    private void showMoreOptions() {
        String[] options = {"Track Order", "Share Order", "Report Issue"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("More Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Toast.makeText(requireContext(), "Track Order", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(requireContext(), "Share Order", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(requireContext(), "Report Issue", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void showCancelOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Xử lý hủy đơn hàng
                    Toast.makeText(requireContext(),
                            "Order " + orderId + " has been cancelled",
                            Toast.LENGTH_LONG).show();

                    // Quay lại màn hình trước
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigateUp();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void confirmOrder() {
        // Tăng bước stepper
        if (currentStep < 4) {
            currentStep++;
            updateStepper();

            String message = "";
            switch (currentStep) {
                case 2:
                    message = "Order confirmed! Preparing your order...";
                    break;
                case 3:
                    message = "Order is on the way!";
                    break;
                case 4:
                    message = "Order delivered successfully!";
                    btnConfirm.setEnabled(false);
                    btnConfirm.setText("Completed");
                    break;
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(),
                    "Order already completed",
                    Toast.LENGTH_SHORT).show();
        }
    }
}