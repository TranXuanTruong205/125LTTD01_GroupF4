package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.TableSessionManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment hiển thị QR thanh toán VietQR
 * Sau 8s tự động gọi API checkout rồi chuyển sang màn hình thành công
 */
public class QRPaymentFragment extends Fragment {

    // Arguments
    public static final String ARG_AMOUNT = "amount";
    public static final String ARG_PAYMENT_METHOD = "payment_method";
    public static final String ARG_ORDER_TYPE = "order_type";
    public static final String ARG_TABLE_ID = "table_id";
    public static final String ARG_CART_ITEM_IDS = "cart_item_ids";

    // VietQR URL template
    private static final String VIETQR_URL = "https://img.vietqr.io/image/mb-0842192393-compact2.png?amount=%d&addInfo=DINERESTAURANT&accountName=PHAM%%20LE%%20THIEU%%20QUANG";

    // Views
    private ImageView ivQRCode, ivPaymentIcon;
    private TextView tvPaymentMethod, tvAmount, tvTimer, tvAccountName, tvTransferContent;
    private ProgressBar progressBar, progressTimer;
    private Button btnConfirmPayment, btnCancel;
    private ImageButton btnBack;

    // State
    private long amount;
    private String paymentMethod;
    private ArrayList<Integer> cartItemIds;
    private CountDownTimer countDownTimer;
    private int countdown = 8; // 8 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments
        if (getArguments() != null) {
            amount = getArguments().getLong(ARG_AMOUNT, 0);
            paymentMethod = getArguments().getString(ARG_PAYMENT_METHOD, "Momo e-wallet");
            cartItemIds = getArguments().getIntegerArrayList(ARG_CART_ITEM_IDS);
        }

        initViews(view);
        setupUI();
        loadQRCode();
        startCountdown();
    }

    private void initViews(View view) {
        ivQRCode = view.findViewById(R.id.ivQRCode);
        ivPaymentIcon = view.findViewById(R.id.ivPaymentIcon);
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvAccountName = view.findViewById(R.id.tvAccountName);
        tvTransferContent = view.findViewById(R.id.tvTransferContent);
        progressBar = view.findViewById(R.id.progressBar);
        progressTimer = view.findViewById(R.id.progressTimer);
        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnBack = view.findViewById(R.id.btnBack);
    }

    private void setupUI() {
        // Payment method
        tvPaymentMethod.setText(paymentMethod);

        // Amount
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvAmount.setText(formatter.format(amount) + " đ");

        // Set payment icon based on method
        setPaymentIcon();

        // Listeners
        btnBack.setOnClickListener(v -> {
            cancelCountdown();
            Navigation.findNavController(v).navigateUp();
        });

        btnCancel.setOnClickListener(v -> {
            cancelCountdown();
            Navigation.findNavController(v).navigateUp();
        });

        btnConfirmPayment.setOnClickListener(v -> {
            cancelCountdown();
            callCheckoutApi();
        });
    }

    private void setPaymentIcon() {
        int iconRes = R.drawable.ic_momo; // default

        if (paymentMethod != null) {
            if (paymentMethod.contains("Momo")) {
                iconRes = R.drawable.ic_momo;
            } else if (paymentMethod.contains("Apple")) {
                iconRes = R.drawable.ic_apple_pay;
            } else if (paymentMethod.contains("PayPal")) {
                iconRes = R.drawable.ic_paypal;
            } else if (paymentMethod.contains("Google")) {
                iconRes = R.drawable.ic_google_pay;
            } else if (paymentMethod.contains("Credit") || paymentMethod.contains("Card")) {
                iconRes = R.drawable.ic_mastercard;
            }
        }

        ivPaymentIcon.setImageResource(iconRes);
    }

    private void loadQRCode() {
        progressBar.setVisibility(View.VISIBLE);

        // Build VietQR URL with amount
        String qrUrl = String.format(Locale.US, VIETQR_URL, amount);

        Glide.with(this)
                .load(qrUrl)
                .placeholder(R.drawable.ic_qr_placeholder)
                .error(R.drawable.ic_qr_placeholder)
                .into(ivQRCode);

        progressBar.setVisibility(View.GONE);
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(countdown * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText("Đang chờ thanh toán... (" + seconds + "s)");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Đang xử lý đơn hàng...");
                callCheckoutApi();
            }
        };
        countDownTimer.start();
    }

    private void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * Gọi API để tạo đơn hàng theo loại order type
     */
    private void callCheckoutApi() {
        TableSessionManager session = TableSessionManager.getInstance(requireContext());

        // Build request body
        Map<String, Object> request = new HashMap<>();

        // Cart item IDs
        if (cartItemIds != null && !cartItemIds.isEmpty()) {
            request.put("cartItemIds", cartItemIds);
        }

        // Payment method
        request.put("paymentMethod", paymentMethod);

        // Table ID (nếu có)
        if (session.hasTableReservation()) {
            try {
                request.put("tableId", Integer.parseInt(session.getTableId()));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // Address ID (nếu delivery)
        String orderType = session.getOrderType();
        if ("delivery".equals(orderType) && session.getAddressId() > 0) {
            request.put("addressId", session.getAddressId());
        }

        // Disable buttons
        btnConfirmPayment.setEnabled(false);
        btnConfirmPayment.setText("Đang xử lý...");
        btnCancel.setEnabled(false);

        // Chọn API endpoint theo order type
        Call<Map<String, Object>> apiCall;
        if ("dine_in".equals(orderType)) {
            // Ăn tại bàn -> /api/orders/onsite
            apiCall = ApiClient.getOrderApi().createOnsiteOrder(request);
        } else if ("takeaway".equals(orderType)) {
            // Mang về -> /api/orders/pickup
            apiCall = ApiClient.getOrderApi().createPickupOrder(request);
        } else {
            // Giao hàng -> /api/orders/delivery
            apiCall = ApiClient.getOrderApi().createDeliveryOrder(request);
        }

        // Call API
        apiCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");
                    if (Boolean.TRUE.equals(success)) {
                        String orderNumber = (String) response.body().get("orderNumber");
                        navigateToSuccess(orderNumber);
                    } else {
                        String msg = (String) response.body().get("message");
                        Toast.makeText(getContext(), msg != null ? msg : "Đặt hàng thất bại", Toast.LENGTH_SHORT)
                                .show();
                        resetButtons();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi đặt hàng", Toast.LENGTH_SHORT).show();
                    resetButtons();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetButtons();
            }
        });
    }

    private void resetButtons() {
        btnConfirmPayment.setEnabled(true);
        btnConfirmPayment.setText("Tôi đã thanh toán");
        btnCancel.setEnabled(true);
    }

    private void navigateToSuccess(String orderNumber) {
        try {
            TableSessionManager session = TableSessionManager.getInstance(requireContext());
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putLong(ARG_AMOUNT, amount);
            bundle.putString(ARG_PAYMENT_METHOD, paymentMethod);
            bundle.putString("order_number", orderNumber);

            // Lấy order type từ session
            if (session.hasOrderSelection()) {
                bundle.putString(ARG_ORDER_TYPE, session.getOrderType());
                bundle.putString("display_address", session.getDisplayAddress());
            }

            if (session.hasTableReservation()) {
                bundle.putString(ARG_TABLE_ID, session.getTableId());
            }

            // Navigate to success
            navController.navigate(R.id.orderSuccessFragment, bundle);

            // Clear session sau khi đặt hàng
            session.clearOrderSelection();
            session.clearTableReservation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelCountdown();
    }
}
