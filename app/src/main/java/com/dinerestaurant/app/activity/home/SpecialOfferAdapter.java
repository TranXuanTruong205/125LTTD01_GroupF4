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

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.ViewHolder> {

    private List<SpecialOfferItem> items;

    public SpecialOfferAdapter(List<SpecialOfferItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_special_offer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecialOfferItem item = items.get(position);
        holder.tvProductName.setText(item.getName());
        holder.tvRating.setText(String.valueOf(item.getRating()));
        holder.tvOriginalPrice.setText("£ " + item.getOriginalPrice());
        holder.tvDiscountPrice.setText("£ " + item.getDiscountPrice());
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
