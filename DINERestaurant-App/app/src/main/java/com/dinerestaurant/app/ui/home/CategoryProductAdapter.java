package com.dinerestaurant.app.ui.home;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CategoryProductItem;
import java.util.List;
public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {
    private List<CategoryProductItem> items;
    private final OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(CategoryProductItem item);
    }
    // Constructor (BỎ AssetManager)
    public CategoryProductAdapter(List<CategoryProductItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    public void setItems(List<CategoryProductItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_product, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryProductItem item = items.get(position);
        // Lưu ý: Đảm bảo class CategoryProductItem của bạn có các hàm getter này
        holder.tvProductName.setText(item.getName());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        holder.tvOriginalPrice.setText(String.format("%,.0fđ", item.getPrice()));
        if (item.getDiscountPrice() != null) {
            holder.tvDiscountPrice.setText(String.format("%,.0fđ", item.getDiscountPrice()));
        } else {
            holder.tvDiscountPrice.setText("");
        }
        // --- GLIDE LOAD ẢNH ---
        String imagePath = item.getImagePath();
        Object imageSource;
        if (imagePath != null && !imagePath.startsWith("http")) {
            imageSource = "file:///android_asset/" + imagePath;
        } else {
            imageSource = imagePath;
        }
        Glide.with(holder.itemView.getContext())
                .load(imageSource)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivProductImage);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvRating, tvOriginalPrice, tvDiscountPrice;
        ViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountPrice = itemView.findViewById(R.id.tvDiscountPrice);
        }
    }
}