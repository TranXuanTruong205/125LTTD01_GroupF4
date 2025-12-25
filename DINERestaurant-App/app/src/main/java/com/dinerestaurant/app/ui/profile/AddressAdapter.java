package com.dinerestaurant.app.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.UserAddress;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private final List<UserAddress> addressList;
    private int selectedPosition = -1;
    private final OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onSelected(UserAddress address);

        void onEdit(UserAddress address);
    }

    public AddressAdapter(List<UserAddress> addressList,
            OnAddressSelectedListener listener) {
        this.addressList = addressList;
        this.listener = listener;

        // AUTO CHECK DEFAULT
        for (int i = 0; i < addressList.size(); i++) {
            if (Boolean.TRUE.equals(addressList.get(i).getIsDefault())) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        UserAddress address = addressList.get(position);

        holder.tvLabel.setText(address.getLabel());
        holder.tvAddress.setText(address.getAddressText());

        holder.imgRadio.setImageResource(
                position == selectedPosition
                        ? R.drawable.bg_radio_checked
                        : R.drawable.bg_radio_unchecked);

        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPosition);

            listener.onSelected(address);
        });
        holder.itemView.setOnLongClickListener(v -> {
            listener.onEdit(address);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    /**
     * Bỏ chọn tất cả địa chỉ
     */
    public void clearSelection() {
        int oldPos = selectedPosition;
        selectedPosition = -1;
        if (oldPos >= 0 && oldPos < addressList.size()) {
            notifyItemChanged(oldPos);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLabel, tvAddress;
        ImageView imgRadio;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            imgRadio = itemView.findViewById(R.id.imgRadio);
        }
    }
}
