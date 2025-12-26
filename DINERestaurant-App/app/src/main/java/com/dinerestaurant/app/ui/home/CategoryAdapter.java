package com.dinerestaurant.app.ui.home;
import android.content.Context;
import android.content.res.AssetManager; // Giữ lại để tránh lỗi constructor cũ nếu còn gọi
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CategoryItem;
import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryItem> items;
    private final OnItemClickListener listener;
    // Context để dùng Glide
    private Context context;
    // Constructor cũ (giữ lại AssetManager nhưng không dùng để tránh lỗi compilation chỗ khác)
    public CategoryAdapter(List<CategoryItem> items, AssetManager assetManager, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    public void setItems(List<CategoryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(CategoryItem item);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.tvCategoryName.setText(item.getName());
        // --- GLIDE LOADING (Phần quan trọng nhất) ---
        String path = item.getImagePath();
        Object imageSource = path;
        // Nếu path không phải http -> coi là asset
        if (path != null && !path.startsWith("http")) {
            imageSource = "file:///android_asset/" + path;
        }
        Glide.with(context)
                .load(imageSource)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivCategoryIcon);

        // --------------------------------------------
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }
    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName;
        ViewHolder(View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}