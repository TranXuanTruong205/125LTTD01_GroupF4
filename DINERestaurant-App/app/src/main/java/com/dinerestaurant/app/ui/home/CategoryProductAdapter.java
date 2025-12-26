package com.dinerestaurant.app.ui.home;
import android.content.Context;
import android.content.res.AssetManager;
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
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(CategoryProductItem item);
    }
    // Giữ constructor có AssetManager để tương thích code cũ
    public CategoryProductAdapter(List<CategoryProductItem> items, AssetManager assetManager, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Thêm setter này nếu cần update list
    public void setItems(List<CategoryProductItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_product, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryProductItem item = items.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        holder.tvOriginalPrice.setText(((int)item.getPrice()) + "đ");
        if (item.getDiscountPrice() != null) {
            holder.tvDiscountPrice.setText(((int)item.getDiscountPrice().doubleValue()) + "đ");
        } else {
            holder.tvDiscountPrice.setText("");
        }
        // --- GLIDE LOADING ---
        String path = item.getImagePath();
        Object imageSource = path;

        if (path != null && !path.startsWith("http")) {
            imageSource = "file:///android_asset/" + path;
        }
        Glide.with(context)
                .load(imageSource)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivProductImage);
        // ---------------------
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage, ivFavorite;
        TextView tvProductName, tvRating, tvOriginalPrice, tvDiscountPrice;
        ViewHolder(View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountPrice = itemView.findViewById(R.id.tvDiscountPrice);
        }
    }
}