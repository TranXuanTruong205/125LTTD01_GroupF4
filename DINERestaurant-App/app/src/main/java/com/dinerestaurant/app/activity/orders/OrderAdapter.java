package com.dinerestaurant.app.activity.orders;

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
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
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
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order ID : " + order.getOrderId());
        holder.tvPrice.setText(order.getFormattedPrice());
        holder.tvStatus.setText(order.getStatus());
        holder.ivFood.setImageResource(order.getFoodImage());

        // Set rating stars
        setRatingStars(holder, order.getRating());

        // Set status color
        setStatusColor(holder, order.getStatus());

        // 👉 Hiện / ẩn stepper
        if (order.isShowStepper()) {
            holder.stepperLayout.setVisibility(View.VISIBLE);
        } else {
            holder.stepperLayout.setVisibility(View.GONE);
        }
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
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
        }
    }

}