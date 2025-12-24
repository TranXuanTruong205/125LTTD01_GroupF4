package com.dinerestaurant.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.repository.OrderRepository;
import com.dinerestaurant.app.model.OrderItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderItem> orderList;
    private List<OrderItem> allOrders;
    private String currentFilter = "All";
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvEmpty;

    private OrderRepository orderRepository;

    private Button btnAll, btnActive, btnCompleted, btnCancelled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Khởi tạo views
        recyclerView = view.findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        tvEmpty = view.findViewById(R.id.tv_empty);

        // Khởi tạo repository
        orderRepository = new OrderRepository(requireContext());

        // Setup Adapter
        allOrders = new ArrayList<>();
        orderList = new ArrayList<>();
        adapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        // Setup filter buttons
        setupFilterButtons(view);

        btnBack = view.findViewById(R.id.imageButton3);
        setupBackButton();

        // Setup SwipeRefresh
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.orange);
            swipeRefresh.setOnRefreshListener(this::loadOrders);
        }

        // Load orders từ API
        loadOrders();

        return view;
    }

    private void loadOrders() {
        showLoading(true);

        orderRepository.getMyOrders().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    Boolean success = (Boolean) res.get("success");

                    if (Boolean.TRUE.equals(success)) {
                        List<Map<String, Object>> data = (List<Map<String, Object>>) res.get("data");

                        if (data != null && !data.isEmpty()) {
                            allOrders.clear();
                            for (Map<String, Object> orderMap : data) {
                                OrderItem item = parseOrderItem(orderMap);
                                allOrders.add(item);
                            }
                            filterOrders(currentFilter);
                            showEmpty(false);
                        } else {
                            allOrders.clear();
                            orderList.clear();
                            adapter.notifyDataSetChanged();
                            showEmpty(true);
                        }
                    } else {
                        showError("Không thể tải danh sách đơn hàng");
                        showEmpty(true);
                    }
                } else {
                    showError("Lỗi tải dữ liệu");
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
                showEmpty(true);
            }
        });
    }

    private OrderItem parseOrderItem(Map<String, Object> map) {
        // Order ID
        Object orderIdObj = map.get("orderId");
        String orderId = orderIdObj != null ? "ĐH" + orderIdObj.toString() : "N/A";

        // Lấy order details để hiển thị tên món đầu tiên
        String foodName = "Đơn hàng";
        List<Map<String, Object>> orderDetails = (List<Map<String, Object>>) map.get("orderDetails");
        if (orderDetails != null && !orderDetails.isEmpty()) {
            Map<String, Object> firstItem = orderDetails.get(0);
            // Lấy thông tin product nếu có
            Map<String, Object> product = (Map<String, Object>) firstItem.get("product");
            if (product != null) {
                Object productName = product.get("productName");
                if (productName != null) {
                    foodName = productName.toString();
                    if (orderDetails.size() > 1) {
                        foodName += " (+" + (orderDetails.size() - 1) + " món)";
                    }
                }
            }
        }

        // Total amount
        double price = 0;
        Object totalObj = map.get("totalAmount");
        if (totalObj instanceof Number) {
            price = ((Number) totalObj).doubleValue();
        }

        // Order status -> map to Active/Completed/Cancelled
        String status = "Active";
        Object statusObj = map.get("orderStatus");
        if (statusObj != null) {
            String apiStatus = statusObj.toString();
            status = mapStatusToDisplay(apiStatus);
        }

        // Rating - mặc định 4 sao
        int rating = 4;

        // Stapper - show cho Active orders
        boolean showStepper = status.equals("Active");

        // Lưu orderId gốc để dùng khi xem chi tiết
        OrderItem item = new OrderItem(orderId, foodName, price, rating, status, R.drawable.nhieumon, showStepper);

        // Lưu orderId số để gọi API
        if (orderIdObj instanceof Number) {
            item.setRealOrderId(((Number) orderIdObj).intValue());
        }

        return item;
    }

    private String mapStatusToDisplay(String apiStatus) {
        if (apiStatus == null)
            return "Active";

        switch (apiStatus) {
            case "Đã đặt":
            case "Đang chuẩn bị":
            case "Đang giao":
                return "Active";
            case "Hoàn thành":
            case "Đã giao":
                return "Completed";
            case "Đã hủy":
                return "Cancelled";
            default:
                return "Active";
        }
    }

    private void setupFilterButtons(View view) {
        btnAll = view.findViewById(R.id.button2);
        btnActive = view.findViewById(R.id.button3);
        btnCompleted = view.findViewById(R.id.button4);
        btnCancelled = view.findViewById(R.id.button6);

        btnAll.setOnClickListener(v -> {
            filterOrders("All");
            updateFilterButtonState("All");
        });
        btnActive.setOnClickListener(v -> {
            filterOrders("Active");
            updateFilterButtonState("Active");
        });
        btnCompleted.setOnClickListener(v -> {
            filterOrders("Completed");
            updateFilterButtonState("Completed");
        });
        btnCancelled.setOnClickListener(v -> {
            filterOrders("Cancelled");
            updateFilterButtonState("Cancelled");
        });
    }

    private void updateFilterButtonState(String selected) {
        // Reset all buttons
        btnAll.setBackgroundResource(R.drawable.rounded_bg);
        btnActive.setBackgroundResource(R.drawable.rounded_bg);
        btnCompleted.setBackgroundResource(R.drawable.rounded_bg);
        btnCancelled.setBackgroundResource(R.drawable.rounded_bg);

        // Highlight selected button
        switch (selected) {
            case "All":
                btnAll.setBackgroundResource(R.drawable.order_nav_color);
                break;
            case "Active":
                btnActive.setBackgroundResource(R.drawable.order_nav_color);
                break;
            case "Completed":
                btnCompleted.setBackgroundResource(R.drawable.order_nav_color);
                break;
            case "Cancelled":
                btnCancelled.setBackgroundResource(R.drawable.order_nav_color);
                break;
        }
    }

    private void filterOrders(String status) {
        currentFilter = status;
        orderList.clear();

        if (status.equals("All")) {
            orderList.addAll(allOrders);
        } else {
            for (OrderItem order : allOrders) {
                if (order.getStatus().equals(status)) {
                    orderList.add(order);
                }
            }
        }

        adapter.notifyDataSetChanged();
        showEmpty(orderList.isEmpty());
    }

    @Override
    public void onOrderClick(OrderItem order) {
        // Navigate to order detail
        NavController navController = Navigation.findNavController(requireView());

        Bundle bundle = new Bundle();
        bundle.putString("order_id", order.getOrderId());
        bundle.putInt("real_order_id", order.getRealOrderId());

        try {
            navController.navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(), "Lỗi điều hướng", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCancelOrderClick(OrderItem order, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận hủy")
                .setMessage("Bạn có chắc muốn hủy đơn hàng này không?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder(order, position))
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelOrder(OrderItem order, int position) {
        showLoading(true);

        orderRepository.cancelOrder(order.getRealOrderId()).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Boolean success = (Boolean) response.body().get("success");

                    if (Boolean.TRUE.equals(success)) {
                        Toast.makeText(getContext(), "Đã hủy đơn hàng!", Toast.LENGTH_SHORT).show();
                        // Refresh list
                        loadOrders();
                    } else {
                        String msg = (String) response.body().get("message");
                        showError(msg != null ? msg : "Không thể hủy đơn hàng");
                    }
                } else {
                    showError("Lỗi hủy đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                showLoading(false);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void setupBackButton() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            });
        }
    }

    private void showLoading(boolean show) {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(show);
        }
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmpty(boolean show) {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh khi quay lại
        loadOrders();
    }
}