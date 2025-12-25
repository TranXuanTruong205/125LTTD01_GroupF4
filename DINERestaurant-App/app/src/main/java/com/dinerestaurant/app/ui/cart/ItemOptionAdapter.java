package com.dinerestaurant.app.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.ItemOption;

import java.util.List;

public class ItemOptionAdapter extends RecyclerView.Adapter<ItemOptionAdapter.OptionViewHolder> {

    private List<ItemOption> options;
    private OnOptionChangeListener listener;

    public interface OnOptionChangeListener {
        void onOptionChanged();
    }

    public ItemOptionAdapter(List<ItemOption> options, OnOptionChangeListener listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        ItemOption option = options.get(position);
        holder.bind(option);
    }

    @Override
    public int getItemCount() {
        return options != null ? options.size() : 0;
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        CheckBox cbOption;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvOptionName);
            tvPrice = itemView.findViewById(R.id.tvOptionPrice);
            cbOption = itemView.findViewById(R.id.cbOption);

            // Bắt sự kiện check
            cbOption.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    options.get(pos).setSelected(isChecked);
                    if (listener != null) listener.onOptionChanged();
                }
            });
        }

        void bind(ItemOption option) {
            // Tạm gỡ listener để tránh loop khi setChecked
            cbOption.setOnCheckedChangeListener(null);

            tvName.setText(option.getOptionName());
            tvPrice.setText(String.format("+ %.0fđ", option.getExtraPrice()));
            cbOption.setChecked(option.isSelected());

            // Gán lại listener
            cbOption.setOnCheckedChangeListener((buttonView, isChecked) -> {
                option.setSelected(isChecked);
                if (listener != null) listener.onOptionChanged();
            });
        }
    }
}