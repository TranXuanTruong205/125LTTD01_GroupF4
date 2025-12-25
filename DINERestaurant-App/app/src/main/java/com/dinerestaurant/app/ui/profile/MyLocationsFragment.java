package com.dinerestaurant.app.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.UserAddressApi;
import com.dinerestaurant.app.model.UserAddress;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyLocationsFragment extends Fragment {

    private UserAddressApi userAddressApi;
    private RecyclerView rvLocations;
    private UserAddress selectedAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_locations, container, false);

        // Back
        view.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity().getSupportFragmentManager().popBackStack()
                );
        view.findViewById(R.id.btnAddLocation).setOnClickListener(v -> {
            showAddAddressDialog();
        });

        rvLocations = view.findViewById(R.id.rvLocations);
        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));

        userAddressApi = ApiClient.getUserAddressApi();

        loadAddresses();

        view.findViewById(R.id.bottomButton).setOnClickListener(v -> {
            if (selectedAddress != null) {
                setDefaultAddress(selectedAddress.getAddressId());
            }
        });

        return view;
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
                                    selectedAddress = address;
                                }

                                @Override
                                public void onEdit(UserAddress address) {
                                    showEditDeleteMenu(address);
                                }
                            }
                    );


                    rvLocations.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setDefaultAddress(int id) {
        userAddressApi.setDefaultAddress(id).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call,
                                   Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
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
                .setTitle("Add Address")
                .setView(dialogView)
                .setPositiveButton("Save", (d, w) -> {

                    UserAddress address = new UserAddress();
                    address.setLabel(edtLabel.getText().toString());
                    address.setAddressText(edtAddress.getText().toString());

                    addAddress(address);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void showEditDeleteMenu(UserAddress address) {

        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(getContext())
                .setTitle("Choose action")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) {
                        // EDIT
                        showEditDialog(address);
                    } else if (which == 1) {
                        // DELETE
                        confirmDelete(address);
                    }
                })
                .show();
    }
    private void confirmDelete(UserAddress address) {

        new AlertDialog.Builder(getContext())
                .setTitle("Delete address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (d, w) -> {
                    deleteAddress(address.getAddressId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteAddress(int id) {

        userAddressApi.deleteAddress(id)
                .enqueue(new Callback<Map<String, String>>() {

                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                                           Response<Map<String, String>> response) {

                        if (response.isSuccessful()) {
                            loadAddresses(); // reload list
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
                    loadAddresses(); // reload list
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

        // ĐỔ DATA CŨ
        edtLabel.setText(address.getLabel());
        edtAddress.setText(address.getAddressText());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Address")
                .setView(dialogView)
                .setPositiveButton("Update", (d, w) -> {

                    address.setLabel(edtLabel.getText().toString());
                    address.setAddressText(edtAddress.getText().toString());

                    updateAddress(address);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void updateAddress(UserAddress address) {

        userAddressApi.updateAddress(
                address.getAddressId(),
                address
        ).enqueue(new Callback<UserAddress>() {

            @Override
            public void onResponse(Call<UserAddress> call,
                                   Response<UserAddress> response) {
                if (response.isSuccessful()) {
                    loadAddresses();   // reload list
                }
            }

            @Override
            public void onFailure(Call<UserAddress> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }



}
