package com.dinerestaurant.app.ui.orders;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.OrderItem;
import com.google.android.material.button.MaterialButton;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderItem> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderItem order);

        void onCancelClick(OrderItem order, int position);
    }

    public OrderAdapter(List<OrderItem> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvPrice.setText(order.getFormattedPrice());
        holder.tvStatus.setText(getVietnameseStatus(order.getStatus()));
        holder.ivFood.setImageResource(order.getFoodImage());

        // Set rating stars
        setRatingStars(holder, order.getRating());

        // Set status color
        setStatusColor(holder, order.getStatus());

        // Hiện / ẩn stepper và cập nhật màu theo trạng thái
        if (order.isShowStepper()) {
            holder.stepperLayout.setVisibility(View.VISIBLE);
            updateStepperByApiStatus(holder, order.getApiStatus());
        } else {
            holder.stepperLayout.setVisibility(View.GONE);
        }

        // Hiển thị nút hủy đơn chỉ khi status = Active
        if (order.getStatus().equals("Active")) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnCancelOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick(order, holder.getAdapterPosition());
                }
            });
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }

        // Click listener - chỉ cho xem chi tiết, không cho Cancelled
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && !order.getStatus().equals("Cancelled")) {
                listener.onOrderClick(order);
            }
        });
    }

    /**
     * Cập nhật màu stepper theo trạng thái API
     */
    private void updateStepperByApiStatus(OrderViewHolder holder, String apiStatus) {
        if (apiStatus == null)
            apiStatus = "Đã đặt";

        int activeColor = Color.parseColor("#FF6B35");
        int grayColor = Color.parseColor("#E0E0E0");

        // Reset all to gray first
        if (holder.step1 != null)
            holder.step1.setCardBackgroundColor(grayColor);
        if (holder.step2 != null)
            holder.step2.setCardBackgroundColor(grayColor);
        if (holder.step3 != null)
            holder.step3.setCardBackgroundColor(grayColor);
        if (holder.step4 != null)
            holder.step4.setCardBackgroundColor(grayColor);
        if (holder.line1 != null)
            holder.line1.setBackgroundColor(grayColor);
        if (holder.line2 != null)
            holder.line2.setBackgroundColor(grayColor);
        if (holder.line3 != null)
            holder.line3.setBackgroundColor(grayColor);

        // Xác định step dựa theo trạng thái
        int step = 1;
        switch (apiStatus) {
            case "Đã đặt":
                step = 1;
                break;
            case "Đã xác nhận":
            case "Đang chuẩn bị":
                step = 2;
                break;
            case "Đang giao":
                step = 3;
                break;
            case "Hoàn thành":
            case "Đã giao":
                step = 4;
                break;
            case "Đã hủy":
                step = 0;
                break;
        }

        // Tô màu active cho các step đã hoàn thành
        if (step >= 1) {
            if (holder.step1 != null)
                holder.step1.setCardBackgroundColor(activeColor);
            if (holder.line1 != null)
                holder.line1.setBackgroundColor(activeColor);
        }
        if (step >= 2) {
            if (holder.step2 != null)
                holder.step2.setCardBackgroundColor(activeColor);
            if (holder.line2 != null)
                holder.line2.setBackgroundColor(activeColor);
        }
        if (step >= 3) {
            if (holder.step3 != null)
                holder.step3.setCardBackgroundColor(activeColor);
            if (holder.line3 != null)
                holder.line3.setBackgroundColor(activeColor);
        }
        if (step >= 4) {
            if (holder.step4 != null)
                holder.step4.setCardBackgroundColor(activeColor);
        }
    }

    private String getVietnameseStatus(String status) {
        if (status == null)
            return "Đang xử lý";
        switch (status) {
            case "Active":
                return "Đang xử lý";
            case "Completed":
                return "Hoàn thành";
            case "Cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private void setRatingStars(OrderViewHolder holder, int rating) {
        TextView[] stars = {
                holder.tvStar1,
                holder.tvStar2,
                holder.tvStar3,
                holder.tvStar4,
                holder.tvStar5
        };

        for (int i = 0; i < stars.length; i++) {
            if (i < rating) {
                stars[i].setText("⭐"); // Filled star
                stars[i].setTextColor(Color.parseColor("#FFD700"));
            } else {
                stars[i].setText("☆"); // Empty star
                stars[i].setTextColor(Color.parseColor("#CCCCCC"));
            }
        }
    }

    private void setStatusColor(OrderViewHolder holder, String status) {
        switch (status) {
            case "Active":
                holder.tvStatus.setTextColor(Color.parseColor("#FF6B35"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFE5DC"));
                break;
            case "Completed":
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                break;
            case "Cancelled":
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
                holder.tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvOrderId;
        TextView tvPrice;
        TextView tvStatus;
        TextView tvStar1, tvStar2, tvStar3, tvStar4, tvStar5;
        View stepperLayout;
        MaterialButton btnCancelOrder;

        // Stepper views
        androidx.cardview.widget.CardView step1, step2, step3, step4;
        View line1, line2, line3;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.iv_food);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvStar1 = itemView.findViewById(R.id.tv_star1);
            tvStar2 = itemView.findViewById(R.id.tv_star2);
            tvStar3 = itemView.findViewById(R.id.tv_star3);
            tvStar4 = itemView.findViewById(R.id.tv_star4);
            tvStar5 = itemView.findViewById(R.id.tv_star5);
            stepperLayout = itemView.findViewById(R.id.layout_stepper);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);

            // Stepper
            step1 = itemView.findViewById(R.id.step1);
            step2 = itemView.findViewById(R.id.step2);
            step3 = itemView.findViewById(R.id.step3);
            step4 = itemView.findViewById(R.id.step4);
            line1 = itemView.findViewById(R.id.line1);
            line2 = itemView.findViewById(R.id.line2);
            line3 = itemView.findViewById(R.id.line3);
        }
    }
}