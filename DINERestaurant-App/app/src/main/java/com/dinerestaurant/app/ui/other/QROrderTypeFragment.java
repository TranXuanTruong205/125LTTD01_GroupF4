package com.dinerestaurant.app.ui.other;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dinerestaurant.app.R;

/**
 * Fragment hiển thị khi quét QR bàn
 * Cho phép chọn: Ăn tại chỗ hoặc Mang về
 */
public class QROrderTypeFragment extends Fragment {

    // Arguments
    private static final String ARG_TABLE_ID = "table_id";
    private static final String ARG_TABLE_NAME = "table_name";

    // Views
    private ImageView btnBack;
    private TextView tvTableInfo;
    private TextView tvDineInDesc;

    private LinearLayout optionDineIn;
    private LinearLayout optionTakeaway;
    private ImageView radioDineIn;
    private ImageView radioTakeaway;

    private LinearLayout sectionDeliveryAddress;
    private LinearLayout addressItem1;
    private TextView tvAddressLabel1;
    private TextView tvAddress1;
    private ImageView radioAddress1;
    private LinearLayout btnAddAddress;

    private Button btnConfirm;

    // State
    private String tableId;
    private String tableName;
    private boolean isDineInSelected = true; // Mặc định chọn Ăn tại chỗ
    private String selectedAddressId = null;

    public static QROrderTypeFragment newInstance(String tableId, String tableName) {
        QROrderTypeFragment fragment = new QROrderTypeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TABLE_ID, tableId);
        args.putString(ARG_TABLE_NAME, tableName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableId = getArguments().getString(ARG_TABLE_ID, "1");
            tableName = getArguments().getString(ARG_TABLE_NAME, "Bàn " + tableId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_order_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
        updateUI();
    }

    private void initViews(View view) {
        btnBack = view.findViewById(R.id.btnBack);
        tvTableInfo = view.findViewById(R.id.tvTableInfo);
        tvDineInDesc = view.findViewById(R.id.tvDineInDesc);

        optionDineIn = view.findViewById(R.id.optionDineIn);
        optionTakeaway = view.findViewById(R.id.optionTakeaway);
        radioDineIn = view.findViewById(R.id.radioDineIn);
        radioTakeaway = view.findViewById(R.id.radioTakeaway);

        sectionDeliveryAddress = view.findViewById(R.id.sectionDeliveryAddress);
        addressItem1 = view.findViewById(R.id.addressItem1);
        tvAddressLabel1 = view.findViewById(R.id.tvAddressLabel1);
        tvAddress1 = view.findViewById(R.id.tvAddress1);
        radioAddress1 = view.findViewById(R.id.radioAddress1);
        btnAddAddress = view.findViewById(R.id.btnAddAddress);

        btnConfirm = view.findViewById(R.id.btnConfirm);
    }

    private void setupListeners() {
        // Nút back
        btnBack.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.popBackStack();
        });

        // Chọn Ăn tại chỗ
        optionDineIn.setOnClickListener(v -> {
            isDineInSelected = true;
            updateSelectionUI();
        });

        // Chọn Mang về
        optionTakeaway.setOnClickListener(v -> {
            isDineInSelected = false;
            updateSelectionUI();
        });

        // Chọn địa chỉ
        addressItem1.setOnClickListener(v -> {
            selectedAddressId = "1";
            radioAddress1.setBackgroundResource(R.drawable.bg_radio_checked);
        });

        // Thêm địa chỉ mới
        btnAddAddress.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Tính năng thêm địa chỉ", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to add address screen
        });

        // Nút xác nhận
        btnConfirm.setOnClickListener(v -> confirmAndNavigate());
    }

    private void updateUI() {
        // Hiển thị thông tin bàn
        tvTableInfo.setText("Bàn " + tableId);
        tvDineInDesc.setText("Đơn hàng sẽ được phục vụ tại Bàn " + tableId);

        // TODO: Load địa chỉ của user từ API
        tvAddressLabel1.setText("Nhà riêng");
        tvAddress1.setText("Địa chỉ mặc định của bạn");

        updateSelectionUI();
    }

    private void updateSelectionUI() {
        if (isDineInSelected) {
            // Ăn tại chỗ được chọn
            radioDineIn.setBackgroundResource(R.drawable.bg_radio_checked);
            radioTakeaway.setBackgroundResource(R.drawable.bg_radio_unchecked);

            // Đổi màu icon
            optionDineIn.findViewById(R.id.radioDineIn).setBackgroundResource(R.drawable.bg_radio_checked);

            // Ẩn section địa chỉ
            sectionDeliveryAddress.setVisibility(View.GONE);

            // Đổi text button
            btnConfirm.setText("Đặt món tại Bàn " + tableId);
        } else {
            // Mang về được chọn
            radioDineIn.setBackgroundResource(R.drawable.bg_radio_unchecked);
            radioTakeaway.setBackgroundResource(R.drawable.bg_radio_checked);

            // Hiện section địa chỉ
            sectionDeliveryAddress.setVisibility(View.VISIBLE);

            // Đổi text button
            btnConfirm.setText("Tiếp tục đặt món");
        }
    }

    private void confirmAndNavigate() {
        try {
            NavController navController = Navigation.findNavController(requireView());

            Bundle bundle = new Bundle();

            if (isDineInSelected) {
                // Ăn tại chỗ
                bundle.putString("order_type", "Tại chỗ");
                bundle.putString("table_id", tableId);
                bundle.putString("table_name", "Bàn " + tableId);
            } else {
                // Mang về
                bundle.putString("order_type", "Mang về");
                if (selectedAddressId != null) {
                    bundle.putString("address_id", selectedAddressId);
                }
            }

            // Navigate đến HomeFragment để chọn món
            navController.navigate(R.id.homeFragment, bundle);

            String message = isDineInSelected
                    ? "Đang order cho Bàn " + tableId
                    : "Đang chọn món mang về";
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
