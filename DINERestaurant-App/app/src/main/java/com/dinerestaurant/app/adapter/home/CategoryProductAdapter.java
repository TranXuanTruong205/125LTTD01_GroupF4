package com.dinerestaurant.app.adapter.home;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.home.CategoryProductItem;
import com.dinerestaurant.app.model.home.SpecialOfferItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CategoryProductAdapter extends RecyclerView.Adapter<CategoryProductAdapter.ViewHolder> {

    private List<CategoryProductItem> items;
    private AssetManager assetManager;
    private CategoryProductAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CategoryProductItem item);
    }
    public CategoryProductAdapter(List<CategoryProductItem> items, AssetManager assetManager, OnItemClickListener listener) {
        this.items = items;
        this.assetManager = assetManager;
        this.listener = listener;
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
        
        // Load ảnh từ assets
        try {
            InputStream is = assetManager.open(item.getImagePath());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.ivProductImage.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Xử lý click
        holder.itemView.setOnClickListener(v -> {
            if(listener != null){
                listener.onItemClick(item);
            }
        });
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
