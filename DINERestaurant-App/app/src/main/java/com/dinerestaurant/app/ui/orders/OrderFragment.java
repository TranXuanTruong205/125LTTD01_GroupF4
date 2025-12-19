package com.dinerestaurant.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.OrderItem;

public class OrderFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<OrderItem> orderList;
    private List<OrderItem> allOrders; // Lưu tất cả orders
    private String currentFilter = "All"; // Bộ lọc hiện tại
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load dữ liệu DEMO
        loadDemoOrders();

        // Setup Adapter
        orderList = new ArrayList<>(allOrders);
        adapter = new OrderAdapter(orderList, this);
        recyclerView.setAdapter(adapter);

        // Setup filter buttons
        setupFilterButtons(view);

        btnBack = view.findViewById(R.id.btnBack);
        setupBackButton();

        return view;
    }

    private void loadDemoOrders() {
        allOrders = new ArrayList<>();

        // Thêm 10 orders DEMO
        allOrders.add(new OrderItem("SP0023900", "Grilled Salmon", 25.20, 4, "Active", R.drawable.nhieumon,true));
        allOrders.add(new OrderItem("SP0023512", "Burger Deluxe", 40.00, 5, "Completed", R.drawable.nhieuthu,false));
        allOrders.add(new OrderItem("SP0023502", "Caesar Salad", 85.00, 4, "Completed", R.drawable.hambogo,false));
        allOrders.add(new OrderItem("SP0023450", "Pasta Carbonara", 20.50, 4, "Cancelled", R.drawable.cake,false));
        allOrders.add(new OrderItem("SP0023401", "Pizza Margherita", 35.00, 5, "Active", R.drawable.comtrung,false));
        allOrders.add(new OrderItem("SP0023302", "Chicken Wings", 18.50, 3, "Completed", R.mipmap.ic_launcher,false));
        allOrders.add(new OrderItem("SP0023201", "Sushi Combo", 55.00, 5, "Active", R.mipmap.ic_launcher,false));
        allOrders.add(new OrderItem("SP0023100", "Steak Medium", 75.00, 4, "Completed", R.mipmap.ic_launcher,false));
        allOrders.add(new OrderItem("SP0023050", "Vegetable Curry", 22.00, 3, "Cancelled", R.mipmap.ic_launcher,false));
        allOrders.add(new OrderItem("SP0023001", "Fish & Chips", 30.00, 4, "Active", R.mipmap.ic_launcher,false));
    }

    private void setupFilterButtons(View view) {
        view.findViewById(R.id.button2).setOnClickListener(v -> filterOrders("All"));
        view.findViewById(R.id.button3).setOnClickListener(v -> filterOrders("Active"));
        view.findViewById(R.id.button4).setOnClickListener(v -> filterOrders("Completed"));
        view.findViewById(R.id.button6).setOnClickListener(v -> filterOrders("Cancelled"));
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
        Toast.makeText(getContext(), "Showing " + status + " orders: " + orderList.size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOrderClick(OrderItem order) {
        // Đã sửa: Điều hướng đến OrderDetailFragment

        NavController navController = Navigation.findNavController(requireView());

        // 2. Tạo Bundle để truyền dữ liệu (Order ID)
        Bundle bundle = new Bundle();
        bundle.putString("order_id", order.getOrderId());

        // (Tùy chọn) Truyền currentStep nếu có trong OrderItem
        // bundle.putInt("current_step", order.getCurrentStep());

        try {
            // Thay thế R.id.action_X_to_Y bằng ID action thực tế trong nav graph của bạn
            navController.navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle);
        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(),
                    "Navigation Error. Check R.id.action_orderFragment_to_orderDetailFragment in nav graph.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void setupBackButton() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Lấy NavController và thực hiện quay lại
                NavController navController = Navigation.findNavController(v);
                navController.navigateUp();
            });
        }
    }
}