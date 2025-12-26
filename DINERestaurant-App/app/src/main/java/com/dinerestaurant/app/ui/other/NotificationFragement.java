package com.dinerestaurant.app.ui.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiNotification;
import com.dinerestaurant.app.model.NotificationItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class NotificationFragement extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton btnMarkAllRead, btnBackHeader;
    private NotificationAdapter adapter;
    private ApiNotification api;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(item -> markNotificationRead(item.getNotificationId()));
        recyclerView.setAdapter(adapter);
        ImageButton btnBackHeader = view.findViewById(R.id.btnBackHeader);
        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.popBackStack(R.id.homeFragment, false);
            });
        }
        // 🔥 BẮT BUỘC init ApiClient
        ApiClient.init(requireContext());
        api = ApiClient.getNotificationApi();

        btnMarkAllRead.setOnClickListener(v -> markAllRead());

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        api.getNotifications().enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call,
                    Response<List<NotificationItem>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                } else {
                    Toast.makeText(getContext(),
                            "Lỗi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Lỗi mạng: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markNotificationRead(int id) {
        api.markNotificationRead(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadNotifications();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    private void markAllRead() {
        api.markAllNotificationsRead().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadNotifications();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }
}
