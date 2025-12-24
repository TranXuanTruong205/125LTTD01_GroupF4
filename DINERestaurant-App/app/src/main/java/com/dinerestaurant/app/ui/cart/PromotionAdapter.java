package com.dinerestaurant.app.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.dto.PromotionResponse;

import java.util.List;

public class PromotionAdapter
        extends RecyclerView.Adapter<PromotionAdapter.ViewHolder> {

    private final List<PromotionResponse> promotions;

    public PromotionAdapter(List<PromotionResponse> promotions) {
        this.promotions = promotions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promotion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder, int position) {

        PromotionResponse promo = promotions.get(position);

        holder.tvTitle.setText(promo.getTitle());
        holder.tvDescription.setText(promo.getDescription());
    }

    @Override
    public int getItemCount() {
        return promotions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription;
        CheckBox cbSelect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }
}
