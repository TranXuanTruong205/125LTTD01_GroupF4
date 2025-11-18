package com.dinerestaurant.app.ui.cart;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends Fragment {

    private ImageButton btnBack;
    private LinearLayout btnGetMore;
    private MaterialButton btnApply;

    private CheckBox cbCash;
    private CheckBox cbMomo;
    private CheckBox cbApplePay;
    private CheckBox cbPayPal;
    private CheckBox cbGooglePay;
    private CheckBox cbCreditCard;

    private List<CheckBox> checkBoxList;
    private String selectedPaymentMethod = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        initViews(view);
        setupCheckBoxes();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        // Khởi tạo views
        btnBack = view.findViewById(R.id.btn_back);
        btnGetMore = view.findViewById(R.id.btn_get_more);
        btnApply = view.findViewById(R.id.btn_apply);

        // Khởi tạo checkboxes
        cbCash = view.findViewById(R.id.cb_free_shipping1);
        cbMomo = view.findViewById(R.id.cb_free_shipping2);
        cbApplePay = view.findViewById(R.id.cb_free_shipping);
        cbPayPal = view.findViewById(R.id.cb_shipping_20);
        cbGooglePay = view.findViewById(R.id.cb_order_20);
        cbCreditCard = view.findViewById(R.id.cb_order_10);
    }

    private void setupCheckBoxes() {
        // Thêm tất cả checkbox vào list
        checkBoxList = new ArrayList<>();
        checkBoxList.add(cbCash);
        checkBoxList.add(cbMomo);
        checkBoxList.add(cbApplePay);
        checkBoxList.add(cbPayPal);
        checkBoxList.add(cbGooglePay);
        checkBoxList.add(cbCreditCard);

        // Set listener cho từng checkbox
        setupCheckBoxListener(cbCash, "Cash");
        setupCheckBoxListener(cbMomo, "Momo e-wallet");
        setupCheckBoxListener(cbApplePay, "Apple Pay");
        setupCheckBoxListener(cbPayPal, "PayPal");
        setupCheckBoxListener(cbGooglePay, "Google Pay");
        setupCheckBoxListener(cbCreditCard, "Credit Card (**** 2259)");
    }

    private void setupCheckBoxListener(CheckBox checkBox, String paymentMethod) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Bỏ chọn tất cả checkbox khác
                for (CheckBox cb : checkBoxList) {
                    if (cb != checkBox) {
                        cb.setChecked(false);
                    }
                }
                // Lưu phương thức thanh toán được chọn
                selectedPaymentMethod = paymentMethod;
            }
        });
    }

    private void setupListeners() {
        // Nút Back
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigateUp();
        });

        // Nút Add New Card
        btnGetMore.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Opening add card screen...", Toast.LENGTH_SHORT).show();
            // TODO: Navigate đến màn hình thêm thẻ mới
            // NavController navController = Navigation.findNavController(v);
            // navController.navigate(R.id.action_paymentFragment_to_addCardFragment);
        });

        // Nút Apply
        btnApply.setOnClickListener(v -> {
            applyPaymentMethod(v);
        });
    }

    private void applyPaymentMethod(View view) {
        // Kiểm tra đã chọn phương thức thanh toán chưa
        if (selectedPaymentMethod.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please select a payment method",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Hiển thị thông báo
        Toast.makeText(requireContext(),
                "Payment method selected: " + selectedPaymentMethod,
                Toast.LENGTH_SHORT).show();

        // Trả kết quả về màn hình trước
        NavController navController = Navigation.findNavController(view);
        Bundle result = new Bundle();
        result.putString("selected_payment_method", selectedPaymentMethod);
        navController.getPreviousBackStackEntry()
                .getSavedStateHandle()
                .set("selected_payment_method", selectedPaymentMethod);

        // Quay lại màn hình trước
        navController.navigateUp();
    }
}