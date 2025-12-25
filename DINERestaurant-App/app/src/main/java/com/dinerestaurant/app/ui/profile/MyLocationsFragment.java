package com.dinerestaurant.app.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.TableSessionManager;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.UserAddressApi;
import com.dinerestaurant.app.model.UserAddress;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment chọn phương thức order
 * 
 * LOGIC:
 * - Chưa đặt bàn: Tại quán DISABLED, Mang về ENABLED (đến lấy), Giao tận nơi
 * ENABLED
 * - Đã đặt bàn/Quét QR: Tại quán ENABLED (Bàn X), Mang về ENABLED (Bàn X - đem
 * về), Giao tận nơi ENABLED
 */
public class MyLocationsFragment extends Fragment {

    // Arguments keys
    public static final String ARG_TABLE_ID = "table_id";
    public static final String ARG_TABLE_NAME = "table_name";
    public static final String ARG_HAS_RESERVATION = "has_reservation"; // Đã đặt bàn/quét QR

    private UserAddressApi userAddressApi;
    private RecyclerView rvLocations;
    private UserAddress selectedAddress;

    // Views
    private LinearLayout optionDineIn;
    private LinearLayout optionTakeaway;
    private ImageView iconDineIn, iconTakeaway;
    private TextView tvDineInTitle, tvDineInDesc;
    private TextView tvTakeawayTitle, tvTakeawayDesc;
    private ImageView radioDineIn, radioTakeaway;

    // State
    private String tableId = null;
    private String tableName = null;
    private boolean hasReservation = false; // Đã có đặt bàn hay chưa
    private String selectedOrderType = null; // "dine_in", "takeaway", "delivery"

    public static MyLocationsFragment newInstance(String tableId, String tableName, boolean hasReservation) {
        MyLocationsFragment fragment = new MyLocationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TABLE_ID, tableId);
        args.putString(ARG_TABLE_NAME, tableName);
        args.putBoolean(ARG_HAS_RESERVATION, hasReservation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ĐỌc từ arguments trước (nếu được truyền từ QR scan)
        if (getArguments() != null) {
            tableId = getArguments().getString(ARG_TABLE_ID);
            tableName = getArguments().getString(ARG_TABLE_NAME);
            hasReservation = getArguments().getBoolean(ARG_HAS_RESERVATION, false);
        }

        // Nếu không có từ arguments, đọc từ SESSION (table đã quét trước đó)
        if (tableId == null || tableId.isEmpty()) {
            TableSessionManager session = TableSessionManager.getInstance(requireContext());
            if (session.hasTableReservation()) {
                tableId = session.getTableId();
                tableName = session.getTableName();
                hasReservation = true;
            }
        }

        // Nếu có table_id thì coi như đã có reservation
        if (tableId != null && !tableId.isEmpty()) {
            hasReservation = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_locations, container, false);

        initViews(view);
        setupListeners(view);
        updateUIByReservationStatus();

        userAddressApi = ApiClient.getUserAddressApi();
        loadAddresses();

        return view;
    }

    private void initViews(View view) {
        rvLocations = view.findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));

        optionDineIn = view.findViewById(R.id.optionDineIn);
        optionTakeaway = view.findViewById(R.id.optionTakeaway);

        iconDineIn = view.findViewById(R.id.iconDineIn);
        iconTakeaway = view.findViewById(R.id.iconTakeaway);

        tvDineInTitle = view.findViewById(R.id.tvDineInTitle);
        tvDineInDesc = view.findViewById(R.id.tvDineInDesc);
        tvTakeawayTitle = view.findViewById(R.id.tvTakeawayTitle);
        tvTakeawayDesc = view.findViewById(R.id.tvTakeawayDesc);

        radioDineIn = view.findViewById(R.id.radioDineIn);
        radioTakeaway = view.findViewById(R.id.radioTakeaway);
    }

    private void setupListeners(View view) {
        // Back
        view.findViewById(R.id.btnBack)
                .setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        // Add Location
        view.findViewById(R.id.btnAddLocation).setOnClickListener(v -> {
            showAddAddressDialog();
        });

        // Apply button
        view.findViewById(R.id.bottomButton).setOnClickListener(v -> {
            applySelection();
        });

        // Tại quán click
        optionDineIn.setOnClickListener(v -> {
            if (hasReservation) {
                selectDineIn();
            } else {
                Toast.makeText(getContext(), "Vui lòng đặt bàn trước để sử dụng chức năng này", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Mang về click - luôn click được
        optionTakeaway.setOnClickListener(v -> {
            selectTakeaway();
        });
    }

    /**
     * Cập nhật UI dựa trên trạng thái đặt bàn
     */
    private void updateUIByReservationStatus() {
        if (hasReservation && tableId != null) {
            // ĐÃ ĐẶT BÀN - Enable Tại quán và hiển thị số bàn
            enableDineInOption();
            updateTakeawayWithTable();
        } else {
            // CHƯA ĐẶT BÀN - Disable Tại quán, Mang về = Đến lấy
            disableDineInOption();
            updateTakeawayWithoutTable();
        }
    }

    /**
     * Enable option Tại quán (khi đã đặt bàn)
     */
    private void enableDineInOption() {
        optionDineIn.setAlpha(1.0f);
        optionDineIn.setClickable(true);

        iconDineIn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.orange));
        tvDineInTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        tvDineInTitle.setText("Ăn tại Bàn " + tableId);
        tvDineInDesc.setText("Phục vụ tại bàn của bạn");
        tvDineInDesc.setTextColor(0xFF666666);
    }

    /**
     * Disable option Tại quán (khi chưa đặt bàn)
     */
    private void disableDineInOption() {
        optionDineIn.setAlpha(0.5f);
        optionDineIn.setClickable(false);

        iconDineIn.setColorFilter(0xFFCCCCCC);
        tvDineInTitle.setTextColor(0xFF999999);
        tvDineInTitle.setText("Ăn tại quán");
        tvDineInDesc.setText("Cần đặt bàn trước");
        tvDineInDesc.setTextColor(0xFFAAAAAA);

        radioDineIn.setBackgroundResource(R.drawable.bg_radio_unchecked);
    }

    /**
     * Update Mang về khi ĐÃ đặt bàn (có ID bàn)
     */
    private void updateTakeawayWithTable() {
        tvTakeawayTitle.setText("Mang về từ Bàn " + tableId);
        tvTakeawayDesc.setText("Đặt món tại bàn và mang về");
    }

    /**
     * Update Mang về khi CHƯA đặt bàn (không có ID bàn)
     */
    private void updateTakeawayWithoutTable() {
        tvTakeawayTitle.setText("Đến lấy");
        tvTakeawayDesc.setText("Đặt món và đến quán lấy");
    }

    private void selectDineIn() {
        selectedOrderType = "dine_in";
        selectedAddress = null;

        // Update UI
        radioDineIn.setBackgroundResource(R.drawable.bg_radio_checked);
        radioTakeaway.setBackgroundResource(R.drawable.bg_radio_unchecked);

        // Bỏ chọn địa chỉ delivery
        clearAddressSelection();
    }

    private void selectTakeaway() {
        selectedOrderType = "takeaway";
        selectedAddress = null;

        // Update UI
        radioDineIn.setBackgroundResource(R.drawable.bg_radio_unchecked);
        radioTakeaway.setBackgroundResource(R.drawable.bg_radio_checked);

        // Bỏ chọn địa chỉ delivery
        clearAddressSelection();
    }

    private void selectDeliveryAddress(UserAddress address) {
        selectedOrderType = "delivery";
        selectedAddress = address;

        // Update UI - bỏ chọn dine-in và takeaway
        radioDineIn.setBackgroundResource(R.drawable.bg_radio_unchecked);
        radioTakeaway.setBackgroundResource(R.drawable.bg_radio_unchecked);
    }

    private void clearAddressSelection() {
        if (rvLocations.getAdapter() instanceof AddressAdapter) {
            ((AddressAdapter) rvLocations.getAdapter()).clearSelection();
        }
    }

    private void applySelection() {
        if (selectedOrderType == null) {
            Toast.makeText(getContext(), "Vui lòng chọn phương thức", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle result = new Bundle();
        result.putString("order_type", selectedOrderType);

        switch (selectedOrderType) {
            case "dine_in":
                // Ăn tại quán - có table_id
                result.putString("table_id", tableId);
                result.putString("table_name", "Bàn " + tableId);
                result.putString("display_address", "Bàn " + tableId);
                break;

            case "takeaway":
                if (hasReservation && tableId != null) {
                    // Mang về từ bàn - có table_id
                    result.putString("table_id", tableId);
                    result.putString("table_name", "Bàn " + tableId);
                    result.putString("display_address", "Mang về - Bàn " + tableId);
                } else {
                    // Đến lấy - không có table_id
                    result.putString("display_address", "Đến quán lấy");
                }
                break;

            case "delivery":
                if (selectedAddress != null) {
                    result.putInt("address_id", selectedAddress.getAddressId());
                    result.putString("address_text", selectedAddress.getAddressText());
                    result.putString("display_address", selectedAddress.getAddressText());
                } else {
                    Toast.makeText(getContext(), "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }

        // LƯU VÀO SESSION - để Cart đọc được
        TableSessionManager session = TableSessionManager.getInstance(requireContext());
        String displayAddress = result.getString("display_address");
        int addressId = result.getInt("address_id", -1);
        session.saveOrderSelection(selectedOrderType, displayAddress, addressId);

        // Gửi result về fragment trước (nếu cần)
        getParentFragmentManager().setFragmentResult("location_result", result);

        Toast.makeText(getContext(), "Đã chọn: " + displayAddress, Toast.LENGTH_SHORT).show();
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void loadAddresses() {
        userAddressApi.getMyAddresses().enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call,
                    Response<List<UserAddress>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressAdapter adapter = new AddressAdapter(
                            response.body(),
                            new AddressAdapter.OnAddressSelectedListener() {

                                @Override
                                public void onSelected(UserAddress address) {
                                    selectDeliveryAddress(address);
                                }

                                @Override
                                public void onEdit(UserAddress address) {
                                    showEditDeleteMenu(address);
                                }
                            });

                    rvLocations.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showAddAddressDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_address, null);

        EditText edtLabel = dialogView.findViewById(R.id.edtLabel);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);

        new AlertDialog.Builder(getContext())
                .setTitle("Thêm địa chỉ mới")
                .setView(dialogView)
                .setPositiveButton("Lưu", (d, w) -> {
                    UserAddress address = new UserAddress();
                    address.setLabel(edtLabel.getText().toString());
                    address.setAddressText(edtAddress.getText().toString());
                    addAddress(address);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showEditDeleteMenu(UserAddress address) {
        String[] options = { "Chỉnh sửa", "Xóa" };

        new AlertDialog.Builder(getContext())
                .setTitle("Chọn thao tác")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(address);
                    } else if (which == 1) {
                        confirmDelete(address);
                    }
                })
                .show();
    }

    private void confirmDelete(UserAddress address) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (d, w) -> {
                    deleteAddress(address.getAddressId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteAddress(int id) {
        userAddressApi.deleteAddress(id)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                            Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                            loadAddresses();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void addAddress(UserAddress address) {
        userAddressApi.addAddress(address).enqueue(new Callback<UserAddress>() {
            @Override
            public void onResponse(Call<UserAddress> call,
                    Response<UserAddress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã thêm địa chỉ mới", Toast.LENGTH_SHORT).show();
                    loadAddresses();
                }
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void showEditDialog(UserAddress address) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_address, null);

        EditText edtLabel = dialogView.findViewById(R.id.edtLabel);
        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);

        edtLabel.setText(address.getLabel());
        edtAddress.setText(address.getAddressText());

        new AlertDialog.Builder(getContext())
                .setTitle("Chỉnh sửa địa chỉ")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (d, w) -> {
                    address.setLabel(edtLabel.getText().toString());
                    address.setAddressText(edtAddress.getText().toString());
                    updateAddress(address);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateAddress(UserAddress address) {
        userAddressApi.updateAddress(
                address.getAddressId(),
                address).enqueue(new Callback<UserAddress>() {
                    @Override
                    public void onResponse(Call<UserAddress> call,
                            Response<UserAddress> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                            loadAddresses();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAddress> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }
}
