package com.dinerestaurant.app.ui.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageButton;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.local.SessionManager;
import com.dinerestaurant.app.data.local.remote.ApiService;
import com.dinerestaurant.app.data.local.remote.RetrofitClient;
import com.dinerestaurant.app.model.NotificationItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragement extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton btnMarkAllRead;
    private NotificationAdapter adapter;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        recyclerView = view.findViewById(R.id.recyclerNotifications);
        btnMarkAllRead = view.findViewById(R.id.btnMarkAllRead);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(item -> {
            // Khi user bấm vào 1 thông báo → gọi API đánh dấu đã đọc
            markNotificationRead(item.getNotificationId());
        });
        recyclerView.setAdapter(adapter);

        btnMarkAllRead.setOnClickListener(v -> markAllRead());

        loadNotifications();

        return view;
    }

    private String getBearerToken() {
        return "Bearer" + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1IiwiaWF0IjoxNzY1NzIxMzYxLCJleHAiOjE3NjU4MDc3NjF9.1GmQcXq9IWITi_1T2zhoEfrYeBhfIq1cLTtlpKOiSwI";
       /* SessionManager sessionManager = new SessionManager(requireContext());
        String token = sessionManager.getToken();
        android.util.Log.d("NOTI_TOKEN", "token=" + token);
        if (token == null) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return null;
        }
        return "Bearer " + token; */
    }

    private void loadNotifications() {
        String bearer = getBearerToken();
        if (bearer == null) return;

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getNotifications(bearer).enqueue(new Callback<List<NotificationItem>>() {
            @Override
            public void onResponse(Call<List<NotificationItem>> call,
                                   Response<List<NotificationItem>> response) {

                android.util.Log.d("NOTI_API", "code=" + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    android.util.Log.d("NOTI_API", "size=" + response.body().size());
                    adapter.setItems(response.body());
                } else {
                    String err = "";
                    try { err = response.errorBody() != null ? response.errorBody().string() : ""; }
                    catch (Exception ignored) {}

                    android.util.Log.e("NOTI_API", "error=" + err);
                    Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NotificationItem>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markNotificationRead(int notificationId) {
        String bearer = getBearerToken();
        if (bearer == null) return;

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.markNotificationRead(bearer, notificationId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // gọi lại load hoặc cập nhật adapter cho nhẹ
                loadNotifications();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // có thể show Toast nếu cần
            }
        });
    }

    private void markAllRead() {
        String bearer = getBearerToken();
        if (bearer == null) return;

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.markAllNotificationsRead(bearer).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // refresh list sau khi đánh dấu hết
                loadNotifications();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) { }
        });
    }
}
