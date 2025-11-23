package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;
import com.google.android.material.button.MaterialButton;

public class PromotionsFragement extends Fragment {

    private ImageButton btnBack;
    private EditText edtPromoCode;
    private Button btnApplyCode;
    private CheckBox cbFreeShipping, cbShipping20, cbOrder20, cbOrder10;
    private LinearLayout btnGetMore;
    private MaterialButton btnApply;

    public PromotionsFragement() {
        // constructor rỗng là bắt buộc cho Fragment
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // inflate layout fragment_promotions
        View view = inflater.inflate(R.layout.fragment_promotions, container, false);

        // Initialize views (dùng view.findViewById)
        btnBack        = view.findViewById(R.id.btn_back);
        edtPromoCode   = view.findViewById(R.id.edt_promo_code);
        btnApplyCode   = view.findViewById(R.id.btn_apply_code);
        cbFreeShipping = view.findViewById(R.id.cb_free_shipping);
        cbShipping20   = view.findViewById(R.id.cb_shipping_20);
        cbOrder20      = view.findViewById(R.id.cb_order_20);
        cbOrder10      = view.findViewById(R.id.cb_order_10);
        btnGetMore     = view.findViewById(R.id.btn_get_more);
        btnApply       = view.findViewById(R.id.btn_apply);

        // Back button: quay lại bằng NavController
        btnBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        // Apply promo code
        btnApplyCode.setOnClickListener(v -> {
            String code = edtPromoCode.getText().toString().trim();
            if (!code.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Applying code: " + code,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Please enter promo code",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Get more promotions
        btnGetMore.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Loading more promotions...",
                        Toast.LENGTH_SHORT).show()
        );

        // Apply button
        btnApply.setOnClickListener(v -> {
            StringBuilder selected = new StringBuilder("Selected: ");

            if (cbFreeShipping.isChecked()) selected.append("FREE SHIPPING, ");
            if (cbShipping20.isChecked())   selected.append("20% OFF Shipping, ");
            if (cbOrder20.isChecked())      selected.append("20% OFF Order, ");
            if (cbOrder10.isChecked())      selected.append("10% OFF Order");

            Toast.makeText(requireContext(),
                    selected.toString(),
                    Toast.LENGTH_LONG).show();
        });

        return view;
    }
}
