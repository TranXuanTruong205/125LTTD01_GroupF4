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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.TableSessionManager;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Fragment hiển thị QR thanh toán VietQR
 * Sau 5-8s tự động chuyển sang màn hình thành công
 */
public class QRPaymentFragment extends Fragment {

    // Arguments
    public static final String ARG_AMOUNT = "amount";
    public static final String ARG_PAYMENT_METHOD = "payment_method";
    public static final String ARG_ORDER_TYPE = "order_type";
    public static final String ARG_TABLE_ID = "table_id";

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
    private CountDownTimer countDownTimer;
    private int countdown = 6; // 6 seconds

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
            navigateToSuccess();
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
                tvTimer.setText("Đang xử lý...");
                navigateToSuccess();
            }
        };
        countDownTimer.start();
    }

    private void cancelCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void navigateToSuccess() {
        try {
            // Clear table session sau khi đặt hàng thành công
            TableSessionManager session = TableSessionManager.getInstance(requireContext());

            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();
            bundle.putLong(ARG_AMOUNT, amount);
            bundle.putString(ARG_PAYMENT_METHOD, paymentMethod);

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
