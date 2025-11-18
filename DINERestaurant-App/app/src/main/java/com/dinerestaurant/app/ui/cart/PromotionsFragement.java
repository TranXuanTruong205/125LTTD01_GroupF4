package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.dinerestaurant.app.R;
import com.google.android.material.button.MaterialButton;

public class PromotionsFragement extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edtPromoCode;
    private Button btnApplyCode;
    private CheckBox cbFreeShipping, cbShipping20, cbOrder20, cbOrder10;
    private LinearLayout btnGetMore;
    private MaterialButton btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_payment);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        edtPromoCode = findViewById(R.id.edt_promo_code);
        btnApplyCode = findViewById(R.id.btn_apply_code);
        cbFreeShipping = findViewById(R.id.cb_free_shipping);
        cbShipping20 = findViewById(R.id.cb_shipping_20);
        cbOrder20 = findViewById(R.id.cb_order_20);
        cbOrder10 = findViewById(R.id.cb_order_10);
        btnGetMore = findViewById(R.id.btn_get_more);
        btnApply = findViewById(R.id.btn_apply);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Apply promo code
        btnApplyCode.setOnClickListener(v -> {
            String code = edtPromoCode.getText().toString().trim();
            if (!code.isEmpty()) {
                Toast.makeText(this, "Applying code: " + code, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter promo code", Toast.LENGTH_SHORT).show();
            }
        });

        // Get more promotions
        btnGetMore.setOnClickListener(v -> {
            Toast.makeText(this, "Loading more promotions...", Toast.LENGTH_SHORT).show();
        });

        // Apply button
        btnApply.setOnClickListener(v -> {
            StringBuilder selected = new StringBuilder("Selected: ");

            if (cbFreeShipping.isChecked()) selected.append("FREE SHIPPING, ");
            if (cbShipping20.isChecked()) selected.append("20% OFF Shipping, ");
            if (cbOrder20.isChecked()) selected.append("20% OFF Order, ");
            if (cbOrder10.isChecked()) selected.append("10% OFF Order");

            Toast.makeText(this, selected.toString(), Toast.LENGTH_LONG).show();
        });
    }
}