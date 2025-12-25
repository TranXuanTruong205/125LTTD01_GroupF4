package com.dinerestaurant.app.ui.reservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.ReservationItem;
import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private final Context context;
    private final List<ReservationItem> reservations;
    private final OnReservationActionListener listener;

    public interface OnReservationActionListener {
        void onCancelClick(ReservationItem reservation, int position);
    }

    public ReservationAdapter(Context context, OnReservationActionListener listener) {
        this.context = context;
        this.reservations = new ArrayList<>();
        this.listener = listener;
    }

    public void setReservations(List<ReservationItem> newReservations) {
        this.reservations.clear();
        this.reservations.addAll(newReservations);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < reservations.size()) {
            reservations.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reservations.size());
        }
    }

    public void updateItemStatus(int position, String newStatus) {
        if (position >= 0 && position < reservations.size()) {
            reservations.get(position).setStatus(newStatus);
            notifyItemChanged(position);
        }
    }

    public boolean isEmpty() {
        return reservations.isEmpty();
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        ReservationItem item = reservations.get(position);

        // Tên bàn
        String tableNumber = item.getTableNumber();
        if (tableNumber != null && !tableNumber.isEmpty()) {
            holder.tvTableName.setText("Bàn số " + tableNumber);
        } else {
            holder.tvTableName.setText("Bàn #" + item.getTableId());
        }

        // QR Code
        if (item.getQrCode() != null && !item.getQrCode().isEmpty()) {
            holder.tvQrCode.setText("Mã: " + item.getQrCode());
            holder.tvQrCode.setVisibility(View.VISIBLE);
        } else {
            holder.tvQrCode.setVisibility(View.GONE);
        }

        // Status
        String status = item.getStatus();
        holder.tvStatus.setText(status != null ? status : "N/A");
        setStatusBackground(holder.tvStatus, holder.layoutHeader, status);

        // Date - format từ yyyy-MM-dd sang dd/MM/yyyy
        String dateStr = item.getReservationDate();
        holder.tvDate.setText(formatDate(dateStr));

        // Time
        String timeStr = item.getReservationTime();
        holder.tvTime.setText(timeStr != null ? timeStr : "N/A");

        // Guest count
        Integer guestCount = item.getGuestCount();
        holder.tvGuestCount.setText(guestCount != null ? guestCount + " khách" : "N/A");

        // Note
        String note = item.getNote();
        if (note != null && !note.isEmpty()) {
            holder.tvNote.setText(note);
            holder.layoutNote.setVisibility(View.VISIBLE);
        } else {
            holder.layoutNote.setVisibility(View.GONE);
        }

        // Cancel button - chỉ hiển thị khi có thể hủy
        if (item.canCancel()) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.layoutActions.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCancelClick(item, holder.getAdapterPosition());
                }
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
        }
    }

    private void setStatusBackground(TextView tvStatus, RelativeLayout layoutHeader, String status) {
        if (status == null) {
            tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            return;
        }

        switch (status) {
            case "Đã xác nhận":
                tvStatus.setBackgroundResource(R.drawable.bg_status_confirmed);
                break;
            case "Đã hủy":
                tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                layoutHeader.setBackgroundColor(context.getResources().getColor(R.color.gray));
                break;
            case "Hoàn thành":
                tvStatus.setBackgroundResource(R.drawable.bg_status_completed);
                layoutHeader.setBackgroundColor(context.getResources().getColor(R.color.gray));
                break;
            case "Chờ xác nhận":
            default:
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                layoutHeader.setBackgroundResource(R.drawable.bg_reservation_header);
                break;
        }
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty())
            return "N/A";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr;
        }
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layoutHeader;
        TextView tvTableName, tvQrCode, tvStatus;
        TextView tvDate, tvTime, tvGuestCount, tvNote;
        LinearLayout layoutNote, layoutActions;
        MaterialButton btnCancel;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutHeader = itemView.findViewById(R.id.layout_header);
            tvTableName = itemView.findViewById(R.id.tv_table_name);
            tvQrCode = itemView.findViewById(R.id.tv_qr_code);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvGuestCount = itemView.findViewById(R.id.tv_guest_count);
            tvNote = itemView.findViewById(R.id.tv_note);
            layoutNote = itemView.findViewById(R.id.layout_note);
            layoutActions = itemView.findViewById(R.id.layout_actions);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
