package com.dinerestaurant.app.ui.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }

    private List<NotificationItem> items = new ArrayList<>();
    private final OnNotificationClickListener listener;

    public NotificationAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<NotificationItem> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    public void markAllRead() {
        for (NotificationItem item : items) {
            item.setRead(true);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMessage, txtCreatedAt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }

        public void bind(final NotificationItem item,
                         final OnNotificationClickListener listener) {
            txtTitle.setText(item.getTitle());
            txtMessage.setText(item.getMessage());
            txtCreatedAt.setText(item.getCreatedAt());

            // Nếu chưa đọc thì in đậm, đọc rồi thì bình thường
            txtTitle.setAlpha(item.isRead() ? 0.6f : 1.0f);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onNotificationClick(item);
            });
        }
    }
}
