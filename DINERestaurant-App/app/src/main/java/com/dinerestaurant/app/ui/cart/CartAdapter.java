package com.dinerestaurant.app.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> items;
    private OnCartAction listener;

    public interface OnCartAction {
        void onIncrease(int cartItemId, int currentQty);
        void onDecrease(int cartItemId, int currentQty);
        void onDelete(int cartItemId);
        void onSelectionChanged();
    }

    public CartAdapter(List<CartItem> items, OnCartAction listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(item.getMenuItem().getItemName());
        holder.tvPrice.setText(item.getMenuItem().getPrice() + " đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item.getCartItemId(), item.getQuantity()));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item.getCartItemId(), item.getQuantity()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item.getCartItemId()));
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onSelectionChanged();
        });
        if (item.getOptions() != null && !item.getOptions().isEmpty()) {
            StringBuilder sb = new StringBuilder("Toppings: ");
            for (int i = 0; i < item.getOptions().size(); i++) {
                sb.append(item.getOptions().get(i).getOptionName());
                if (i < item.getOptions().size() - 1) sb.append(", ");
            }
            holder.tvOptions.setText(sb.toString());
            holder.tvOptions.setVisibility(View.VISIBLE);
        } else {
            holder.tvOptions.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return items == null ? 0 : items.size(); }
    public List<CartItem> getItems() {
        return items;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnPlus, btnMinus, btnDelete;
        ImageView imgProduct;
        TextView tvOptions;
        CheckBox cbSelect;
        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvProductName);
            tvPrice = view.findViewById(R.id.tvProductPrice);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            btnPlus = view.findViewById(R.id.btnPlus);
            btnMinus = view.findViewById(R.id.btnMinus);
            btnDelete = view.findViewById(R.id.btnDelete);
            imgProduct = view.findViewById(R.id.ivProductImage);
            tvOptions = view.findViewById(R.id.tvProductOptions);
            cbSelect = view.findViewById(R.id.cbSelect);
        }
    }
}