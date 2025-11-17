package com.dinerestaurant.app.activity.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import java.util.List;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    private List<CategoryProductItem> items;

    public CategoryProductAdapter(List<CategoryProductItem> items) {
        this.items = items;
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
        holder.tvProductName.setText(item.getName());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        holder.tvOriginalPrice.setText(item.getOriginalPrice() + "đ");
        holder.tvDiscountPrice.setText(item.getDiscountPrice() + "đ");
    }

    @Override
    public int getItemCount() {
        return items.size();
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
