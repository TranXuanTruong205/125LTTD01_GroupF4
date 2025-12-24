package com.dinerestaurant.app.ui.cart;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;
import java.util.List;
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private List<CartItem> items;
    private OnCartAction listener;
    private AssetManager assetManager; // Thêm biến assetManager
    public interface OnCartAction {
        void onIncrease(int id, int qty);
        void onDecrease(int id, int qty);
        void onDelete(int id);
        void onSelectionChanged();
    }
    public CartAdapter(List<CartItem> items, AssetManager assetManager, OnCartAction listener) {
        this.items = items;
        this.assetManager = assetManager; // Gán qua constructor
        this.listener = listener;
    }
    public void updateData(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.tvName.setText(item.getMenuItem().getItemName());
        holder.tvPrice.setText(String.format("%.0f đ", item.getMenuItem().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        // --- CODE LOAD ẢNH TỪ ASSETS ---
        try {
            String imagePath = item.getMenuItem().getImage(); // Ví dụ: "burger.png"
            InputStream is = assetManager.open(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.imgProduct.setImageBitmap(bitmap);
            is.close();
        } catch (Exception e) {
            holder.imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        // --- CÁC EVENT CLICK ---
        holder.btnPlus.setOnClickListener(v -> listener.onIncrease(item.getCartItemId(), item.getQuantity()));
        holder.btnMinus.setOnClickListener(v -> listener.onDecrease(item.getCartItemId(), item.getQuantity()));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item.getCartItemId()));
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(item.isSelected());
        holder.cbSelect.setOnCheckedChangeListener((bv, isChecked) -> {
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
    public List<CartItem> getItems() { return items; }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvOptions;
        ImageButton btnPlus, btnMinus, btnDelete;
        ImageView imgProduct;
        CheckBox cbSelect;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvProductName);
            tvPrice = v.findViewById(R.id.tvProductPrice);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvOptions = v.findViewById(R.id.tvProductOptions);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
            btnDelete = v.findViewById(R.id.btnDelete);
            imgProduct = v.findViewById(R.id.ivProductImage);
            cbSelect = v.findViewById(R.id.cbSelect);
        }
    }
}