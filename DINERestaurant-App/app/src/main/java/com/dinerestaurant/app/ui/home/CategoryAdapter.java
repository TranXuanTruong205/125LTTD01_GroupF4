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
import com.dinerestaurant.app.model.CategoryItem;
import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<CategoryItem> items;
    private final OnItemClickListener listener;
    // Không cần AssetManager nữa vì Glide lo rồi
    public CategoryAdapter(List<CategoryItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }
    // Hàm cập nhật data
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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = items.get(position);
        holder.tvCategoryName.setText(item.getName());
        String path = item.getImagePath();

        // Logic xử lý đường dẫn thông minh
        Object imageSource;
        if (path != null && !path.startsWith("http")) {
            // Nếu là đường dẫn assets (ví dụ: images/categories/burger.png)
            // Glide cần prefix "file:///android_asset/" để hiểu
            imageSource = "file:///android_asset/" + path;
        } else {
            // Nếu là URL (upload ảnh sau này) hoặc null
            imageSource = path;
        }
        // Load ảnh bằng Glide
        Glide.with(holder.itemView.getContext())
                .load(imageSource)
                .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh hiển thị khi đang load
                .error(android.R.drawable.ic_menu_gallery)       // Ảnh hiển thị khi lỗi
                .into(holder.ivCategoryIcon);
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