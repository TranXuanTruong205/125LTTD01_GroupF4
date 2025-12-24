package com.dinerestaurant.app.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.PromotionApi;
import com.dinerestaurant.app.data.remote.dto.PromotionResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionsFragement extends Fragment {

    // ================== UI ==================
    private ImageButton btnBack;
    private EditText edtPromoCode;
    private Button btnApplyCode;
    private LinearLayout btnGetMore;
    private MaterialButton btnApply;

    // ================== RecyclerView ==================
    private RecyclerView rvPromotions;
    private PromotionAdapter promotionAdapter;

    ;

    public PromotionsFragement() {
        // constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_promotions,
                container,
                false
        );

        // ================== FIND VIEW ==================
        btnBack      = view.findViewById(R.id.btn_back);
        edtPromoCode = view.findViewById(R.id.edt_promo_code);
        btnApplyCode = view.findViewById(R.id.btn_apply_code);
        btnGetMore   = view.findViewById(R.id.btn_get_more);
        btnApply     = view.findViewById(R.id.btn_apply);

        rvPromotions = view.findViewById(R.id.rv_promotions);
        rvPromotions.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        // ================== ACTION ==================
        btnBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp()
        );

        btnApplyCode.setOnClickListener(v -> {
            String code = edtPromoCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(
                        requireContext(),
                        "Please enter promo code",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                Toast.makeText(
                        requireContext(),
                        "Apply later when cart available",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        btnGetMore.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Load more promotions later",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnApply.setOnClickListener(v ->
                Toast.makeText(
                        requireContext(),
                        "Cart not implemented yet",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // ================== CALL API ==================
        loadPromotions();

        return view;
    }

    // ================== CALL API GET PROMOTIONS ==================
    private void loadPromotions() {

        PromotionApi api =
                ApiClient.getClient().create(PromotionApi.class);

        api.getPromotions().enqueue(
                new Callback<List<PromotionResponse>>() {

                    @Override
                    public void onResponse(
                            Call<List<PromotionResponse>> call,
                            Response<List<PromotionResponse>> response) {

                        if (!response.isSuccessful()
                                || response.body() == null) return;

                        promotionAdapter =
                                new PromotionAdapter(response.body());

                        rvPromotions.setAdapter(promotionAdapter);
                    }

                    @Override
                    public void onFailure(
                            Call<List<PromotionResponse>> call,
                            Throwable t) {

                        Toast.makeText(
                                requireContext(),
                                "Load promotions failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
