package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Fragment hiển thị đặt hàng thành công
 */
public class OrderSuccessFragment extends Fragment {

    // Arguments
    public static final String ARG_AMOUNT = "amount";
    public static final String ARG_PAYMENT_METHOD = "payment_method";
    public static final String ARG_ORDER_TYPE = "order_type";
    public static final String ARG_TABLE_ID = "table_id";
    public static final String ARG_DISPLAY_ADDRESS = "display_address";

    // Views
    private TextView tvOrderId, tvOrderType, tvPaymentMethod, tvTotalAmount, tvDeliveryInfo;
    private Button btnViewOrder, btnBackHome;

    // State
    private long amount;
    private String paymentMethod;
    private String orderType;
    private String tableId;
    private String displayAddress;
    private String orderId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments
        if (getArguments() != null) {
            amount = getArguments().getLong(ARG_AMOUNT, 0);
            paymentMethod = getArguments().getString(ARG_PAYMENT_METHOD, "");
            orderType = getArguments().getString(ARG_ORDER_TYPE, "");
            tableId = getArguments().getString(ARG_TABLE_ID, "");
            displayAddress = getArguments().getString(ARG_DISPLAY_ADDRESS, "");
        }

        // Generate order ID
        orderId = generateOrderId();

        initViews(view);
        displayOrderInfo();
    }

    private void initViews(View view) {
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrderType = view.findViewById(R.id.tvOrderType);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvDeliveryInfo = view.findViewById(R.id.tvDeliveryInfo);
        btnViewOrder = view.findViewById(R.id.btnViewOrder);
        btnBackHome = view.findViewById(R.id.btnBackHome);

        // Listeners
        btnViewOrder.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(v);
                // Navigate to orders tab
                navController.navigate(R.id.ordersFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnBackHome.setOnClickListener(v -> {
            try {
                NavController navController = Navigation.findNavController(v);
                // Navigate to home
                navController.navigate(R.id.homeFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void displayOrderInfo() {
        // Order ID
        tvOrderId.setText("#" + orderId);

        // Order type
        String orderTypeDisplay = getOrderTypeDisplay();
        tvOrderType.setText(orderTypeDisplay);

        // Payment method
        tvPaymentMethod.setText(paymentMethod != null ? paymentMethod : "Cash");

        // Total amount
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalAmount.setText(formatter.format(amount) + " đ");

        // Delivery info
        String deliveryInfo = getDeliveryInfo();
        tvDeliveryInfo.setText(deliveryInfo);
    }

    private String getOrderTypeDisplay() {
        if (orderType == null)
            return "N/A";

        switch (orderType) {
            case "dine_in":
                return "Ăn tại quán" + (tableId != null && !tableId.isEmpty() ? " - Bàn " + tableId : "");
            case "takeaway":
                if (tableId != null && !tableId.isEmpty()) {
                    return "Mang về - Bàn " + tableId;
                } else {
                    return "Đến lấy";
                }
            case "delivery":
                return "Giao tận nơi";
            default:
                return displayAddress != null ? displayAddress : "N/A";
        }
    }

    private String getDeliveryInfo() {
        if (orderType == null)
            return "";

        switch (orderType) {
            case "dine_in":
                return "🍽️ Đơn hàng sẽ được phục vụ tại " +
                        (tableId != null && !tableId.isEmpty() ? "Bàn " + tableId : "quán");
            case "takeaway":
                if (tableId != null && !tableId.isEmpty()) {
                    return "📦 Đơn hàng sẽ được chuẩn bị tại Bàn " + tableId;
                } else {
                    return "📦 Vui lòng đến quán để nhận đơn hàng";
                }
            case "delivery":
                return "🚚 Đơn hàng sẽ được giao đến: " +
                        (displayAddress != null ? displayAddress : "địa chỉ của bạn");
            default:
                return "";
        }
    }

    private String generateOrderId() {
        // Format: ORD + timestamp + random
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd", Locale.getDefault());
        String dateStr = sdf.format(new Date());
        int random = new Random().nextInt(9000) + 1000; // 1000-9999
        return "ORD" + dateStr + random;
    }
}
