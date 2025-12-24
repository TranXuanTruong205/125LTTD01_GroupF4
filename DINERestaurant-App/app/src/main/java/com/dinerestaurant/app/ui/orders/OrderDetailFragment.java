package com.dinerestaurant.app.ui.orders;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.repository.OrderRepository;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailFragment extends Fragment {

    // Header views
    private ImageButton btnBack;
    private ImageButton btnMore;
    private TextView tvOrderId;
    private TextView tvStatusBadge;

    // Stepper views
    private CardView step1, step2, step3, step4;
    private View line1, line2, line3;

    // Delivery & Payment views
    private TextView tvAddress, tvAddressLabel;
    private TextView tvCash;
    private TextView tvOrderType;

    // Total views
    private TextView tvSubtotalValue;
    private TextView tvDeliveryValue;
    private TextView tvDiscountValue;
    private TextView tvTotalValue;

    // Bottom buttons
    private Button btnCancel;
    private Button btnConfirm;

    // Loading
    private ProgressBar progressBar;

    // Data
    private int realOrderId;
    private String orderId;
    private String orderStatus;
    private OrderRepository orderRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderRepository = new OrderRepository(requireContext());

        // Get arguments
        if (getArguments() != null) {
            orderId = getArguments().getString("order_id", "N/A");
            realOrderId = getArguments().getInt("real_order_id", 0);
        }

        setupListeners();
        loadOrderDetail();
    }

    private void initViews(View view) {
        // Header
        btnBack = view.findViewById(R.id.imageButton3);
        btnMore = view.findViewById(R.id.imageButton);
        tvOrderId = view.findViewById(R.id.textView2);
        tvStatusBadge = view.findViewById(R.id.tv_status_badge);

        // Stepper
        step1 = view.findViewById(R.id.step1);
        step2 = view.findViewById(R.id.step2);
        step3 = view.findViewById(R.id.step3);
        step4 = view.findViewById(R.id.step4);
        line1 = view.findViewById(R.id.line1);
        line2 = view.findViewById(R.id.line2);
        line3 = view.findViewById(R.id.line3);

        // Delivery & Payment
        tvAddress = view.findViewById(R.id.tv_address);
        tvAddressLabel = view.findViewById(R.id.tv_address_id2);
        tvCash = view.findViewById(R.id.tv_cash);

        // Totals
        tvSubtotalValue = view.findViewById(R.id.tv_subtotal_value);
        tvDeliveryValue = view.findViewById(R.id.tv_delivery_value);
        tvDiscountValue = view.findViewById(R.id.tv_discount_value);

        // Bottom buttons
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // Progress (Có thể thêm trong layout nếu cần)
        // progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        btnMore.setOnClickListener(v -> showMoreOptions());

        btnCancel.setOnClickListener(v -> showCancelOrderDialog());

        btnConfirm.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Đơn hàng đang được xử lý", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadOrderDetail() {
        if (realOrderId == 0) {
            Toast.makeText(requireContext(), "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        tvOrderId.setText(orderId);

        orderRepository.getOrderById(realOrderId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    Boolean success = (Boolean) res.get("success");

                    if (Boolean.TRUE.equals(success)) {
                        Map<String, Object> orderData = (Map<String, Object>) res.get("data");
                        if (orderData != null) {
                            displayOrderDetail(orderData);
                        }
                    } else {
                        String msg = (String) res.get("message");
                        Toast.makeText(requireContext(), msg != null ? msg : "Lỗi tải đơn hàng", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayOrderDetail(Map<String, Object> order) {
        // Order status
        orderStatus = (String) order.get("orderStatus");
        if (tvStatusBadge != null && orderStatus != null) {
            tvStatusBadge.setText(orderStatus);
            setStatusBadgeColor(orderStatus);
        }
        updateStepperByStatus(orderStatus);

        // Order type
        String orderType = (String) order.get("orderType");
        if (tvAddressLabel != null && orderType != null) {
            switch (orderType) {
                case "Giao hàng":
                    tvAddressLabel.setText("Giao hàng đến");
                    break;
                case "Mang về":
                    tvAddressLabel.setText("Đơn mang về");
                    break;
                case "Tại chỗ":
                    tvAddressLabel.setText("Ăn tại chỗ");
                    break;
            }
        }

        // Address
        String address = (String) order.get("deliveryAddress");
        if (tvAddress != null) {
            tvAddress.setText(address != null && !address.isEmpty() ? address : "Không có địa chỉ");
        }

        // Payment method
        String paymentMethod = (String) order.get("paymentMethod");
        if (tvCash != null) {
            tvCash.setText(paymentMethod != null ? paymentMethod : "Tiền mặt");
        }

        // Total amount
        Object totalObj = order.get("totalAmount");
        double total = 0;
        if (totalObj instanceof Number) {
            total = ((Number) totalObj).doubleValue();
        }
        if (tvSubtotalValue != null) {
            tvSubtotalValue.setText(formatPrice(total));
        }

        // Delivery fee
        Object deliveryFeeObj = order.get("deliveryFee");
        double deliveryFee = 0;
        if (deliveryFeeObj instanceof Number) {
            deliveryFee = ((Number) deliveryFeeObj).doubleValue();
        }
        if (tvDeliveryValue != null) {
            if (deliveryFee == 0) {
                tvDeliveryValue.setText("MIỄN PHÍ");
                tvDeliveryValue.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvDeliveryValue.setText(formatPrice(deliveryFee));
            }
        }

        // Discount (nếu có)
        if (tvDiscountValue != null) {
            tvDiscountValue.setText("0₫");
        }

        // Order items
        List<Map<String, Object>> orderDetails = (List<Map<String, Object>>) order.get("orderDetails");
        if (orderDetails != null) {
            displayOrderItems(orderDetails);
        }

        // Update buttons based on status
        updateButtonsByStatus(orderStatus);
    }

    private void displayOrderItems(List<Map<String, Object>> items) {
        // Nếu có RecyclerView riêng hoặc dùng layout có sẵn
        // Ở đây ta cập nhật các TextView có sẵn trong layout

        // Layout hiện tại có 3 card món cố định, ta sẽ ẩn/hiện và cập nhật text
        // Cách tốt hơn là dùng RecyclerView, nhưng giữ nguyên layout hiện tại

        View view = getView();
        if (view == null)
            return;

        // Card 1
        TextView tvName1 = view.findViewById(R.id.tv_order_id0);
        TextView tvPrice1 = view.findViewById(R.id.tv_price8);
        TextView tvQty1 = view.findViewById(R.id.tv_price2);

        // Card 2
        TextView tvName2 = view.findViewById(R.id.tv_order_id3);
        TextView tvPrice2 = view.findViewById(R.id.tv_price5);
        TextView tvQty2 = view.findViewById(R.id.tv_price4);

        // Card 3
        TextView tvName3 = view.findViewById(R.id.tv_order_id);
        TextView tvPrice3 = view.findViewById(R.id.tv_price9);
        TextView tvQty3 = view.findViewById(R.id.tv_price7);

        // Ẩn tất cả trước
        View card1 = view.findViewById(R.id.iv_food1);
        View card2 = view.findViewById(R.id.iv_food3);
        View card3 = view.findViewById(R.id.iv_food);

        // Hiển thị theo số lượng items
        for (int i = 0; i < items.size() && i < 3; i++) {
            Map<String, Object> item = items.get(i);

            String productName = "Món ăn";
            // Nếu có thông tin product từ API
            // productName = ...

            Object qtyObj = item.get("quantity");
            int qty = qtyObj instanceof Number ? ((Number) qtyObj).intValue() : 1;

            Object unitPriceObj = item.get("unitPrice");
            double unitPrice = unitPriceObj instanceof Number ? ((Number) unitPriceObj).doubleValue() : 0;

            Object subtotalObj = item.get("subtotal");
            double subtotal = subtotalObj instanceof Number ? ((Number) subtotalObj).doubleValue() : 0;

            switch (i) {
                case 0:
                    if (tvName1 != null)
                        tvName1.setText("Món " + (i + 1));
                    if (tvQty1 != null)
                        tvQty1.setText("x" + qty);
                    if (tvPrice1 != null)
                        tvPrice1.setText(formatPrice(subtotal));
                    break;
                case 1:
                    if (tvName2 != null)
                        tvName2.setText("Món " + (i + 1));
                    if (tvQty2 != null)
                        tvQty2.setText("x" + qty);
                    if (tvPrice2 != null)
                        tvPrice2.setText(formatPrice(subtotal));
                    break;
                case 2:
                    if (tvName3 != null)
                        tvName3.setText("Món " + (i + 1));
                    if (tvQty3 != null)
                        tvQty3.setText("x" + qty);
                    if (tvPrice3 != null)
                        tvPrice3.setText(formatPrice(subtotal));
                    break;
            }
        }
    }

    private void setStatusBadgeColor(String status) {
        if (status == null || tvStatusBadge == null)
            return;

        int bgColor, textColor;
        switch (status) {
            case "Đã đặt":
            case "Đang chuẩn bị":
                textColor = getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "Đang giao":
                textColor = getResources().getColor(android.R.color.holo_blue_dark);
                break;
            case "Hoàn thành":
            case "Đã giao":
                textColor = getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "Đã hủy":
                textColor = getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                textColor = getResources().getColor(android.R.color.darker_gray);
        }
        tvStatusBadge.setTextColor(textColor);
    }

    private void updateStepperByStatus(String status) {
        if (status == null)
            return;

        resetStepperColors();

        int step = 1;
        switch (status) {
            case "Đã đặt":
                step = 1;
                break;
            case "Đang chuẩn bị":
                step = 2;
                break;
            case "Đang giao":
                step = 3;
                break;
            case "Hoàn thành":
            case "Đã giao":
                step = 4;
                break;
            case "Đã hủy":
                step = 0; // Không hiển thị stepper
                break;
        }

        updateStepperToStep(step);
    }

    private void resetStepperColors() {
        int grayColor = getResources().getColor(android.R.color.darker_gray);
        if (step1 != null)
            step1.setCardBackgroundColor(grayColor);
        if (step2 != null)
            step2.setCardBackgroundColor(grayColor);
        if (step3 != null)
            step3.setCardBackgroundColor(grayColor);
        if (step4 != null)
            step4.setCardBackgroundColor(grayColor);
        if (line1 != null)
            line1.setBackgroundColor(grayColor);
        if (line2 != null)
            line2.setBackgroundColor(grayColor);
        if (line3 != null)
            line3.setBackgroundColor(grayColor);
    }

    private void updateStepperToStep(int step) {
        int activeColor = getResources().getColor(android.R.color.holo_orange_dark);

        if (step >= 1) {
            if (step1 != null)
                step1.setCardBackgroundColor(activeColor);
            if (line1 != null)
                line1.setBackgroundColor(activeColor);
        }
        if (step >= 2) {
            if (step2 != null)
                step2.setCardBackgroundColor(activeColor);
            if (line2 != null)
                line2.setBackgroundColor(activeColor);
        }
        if (step >= 3) {
            if (step3 != null)
                step3.setCardBackgroundColor(activeColor);
            if (line3 != null)
                line3.setBackgroundColor(activeColor);
        }
        if (step >= 4) {
            if (step4 != null)
                step4.setCardBackgroundColor(activeColor);
        }
    }

    private void updateButtonsByStatus(String status) {
        if (status == null)
            return;

        switch (status) {
            case "Đã hủy":
                btnCancel.setVisibility(View.GONE);
                btnConfirm.setText("Đã hủy");
                btnConfirm.setEnabled(false);
                break;
            case "Hoàn thành":
            case "Đã giao":
                btnCancel.setVisibility(View.GONE);
                btnConfirm.setText("Hoàn thành");
                btnConfirm.setEnabled(false);
                break;
            case "Đang giao":
                btnCancel.setVisibility(View.GONE);
                btnConfirm.setText("Đang giao");
                btnConfirm.setEnabled(false);
                break;
            default:
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("Hủy đơn");
                btnConfirm.setText("Theo dõi");
                btnConfirm.setEnabled(true);
        }
    }

    private void showMoreOptions() {
        String[] options = { "Theo dõi đơn hàng", "Chia sẻ đơn", "Báo cáo vấn đề" };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tùy chọn")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            refreshOrderStatus();
                            break;
                        case 1:
                            Toast.makeText(requireContext(), "Chia sẻ đơn hàng", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(requireContext(), "Báo cáo vấn đề", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .show();
    }

    private void refreshOrderStatus() {
        orderRepository.getOrderStatus(realOrderId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    if (Boolean.TRUE.equals(success)) {
                        String newStatus = (String) response.body().get("status");
                        if (newStatus != null) {
                            orderStatus = newStatus;
                            if (tvStatusBadge != null) {
                                tvStatusBadge.setText(newStatus);
                                setStatusBadgeColor(newStatus);
                            }
                            updateStepperByStatus(newStatus);
                            updateButtonsByStatus(newStatus);
                            Toast.makeText(requireContext(), "Trạng thái: " + newStatus, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCancelOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> cancelOrder())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelOrder() {
        orderRepository.cancelOrder(realOrderId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        Toast.makeText(requireContext(), "Đã hủy đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                        // Quay lại màn trước
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigateUp();
                    } else {
                        String msg = (String) response.body().get("message");
                        Toast.makeText(requireContext(), msg != null ? msg : "Không thể hủy đơn", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Lỗi hủy đơn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatPrice(double price) {
        return String.format("%,.0f₫", price);
    }
}