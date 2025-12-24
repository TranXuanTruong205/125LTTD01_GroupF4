package com.dinerestaurant.app.ui.reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.TableItem;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private final Context context;
    private final List<TableItem> tables;
    private final OnTableClickListener listener;
    private int selectedPosition = -1;

    public interface OnTableClickListener {
        void onTableClick(TableItem table);
    }

    public TableAdapter(Context context, OnTableClickListener listener) {
        this.context = context;
        this.tables = new ArrayList<>();
        this.listener = listener;
    }

    public void setTables(List<TableItem> newTables) {
        this.tables.clear();
        this.tables.addAll(newTables);
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        int oldPosition = selectedPosition;
        selectedPosition = -1;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        TableItem table = tables.get(position);
        boolean isSelected = position == selectedPosition;

        holder.tvTableNumber.setText("Bàn " + table.getTableNumber());
        holder.tvCapacity.setText(table.getCapacity() + " chỗ");
        holder.tvStatus.setText("Còn trống");

        // Thay đổi UI khi chọn
        if (isSelected) {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_table_item_selected);
            holder.ivTableIcon.setColorFilter(ContextCompat.getColor(context, R.color.orange));
        } else {
            holder.layoutItem.setBackgroundResource(R.drawable.bg_table_item_unselected);
            holder.ivTableIcon.setColorFilter(ContextCompat.getColor(context, R.color.orange));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            if (previousSelected != -1) {
                notifyItemChanged(previousSelected);
            }
            notifyItemChanged(selectedPosition);

            listener.onTableClick(table);
        });
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutItem;
        ImageView ivTableIcon;
        TextView tvTableNumber;
        TextView tvCapacity;
        TextView tvStatus;

        TableViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_table_item);
            ivTableIcon = itemView.findViewById(R.id.iv_table_icon);
            tvTableNumber = itemView.findViewById(R.id.tv_table_number);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }
    }
}
