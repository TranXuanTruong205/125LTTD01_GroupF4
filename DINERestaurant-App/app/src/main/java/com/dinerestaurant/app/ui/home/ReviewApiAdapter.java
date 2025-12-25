package com.dinerestaurant.app.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.dto.ReviewResponse;

import java.util.List;

public class ReviewApiAdapter extends RecyclerView.Adapter<ReviewApiAdapter.ViewHolder> {

    private final List<ReviewResponse> reviews;

    public ReviewApiAdapter(List<ReviewResponse> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewResponse r = reviews.get(position);

        // ❗ API KHÔNG CÓ userName → hiển thị tạm
        holder.tvReviewerName.setText(r.getUserName());

        holder.tvReviewDate.setText(r.createdAt);
        holder.tvReviewText.setText(r.comment);

        // Rating stars
        ImageView[] stars = {
                holder.ivStar1, holder.ivStar2,
                holder.ivStar3, holder.ivStar4, holder.ivStar5
        };

        for (int i = 0; i < stars.length; i++) {
            stars[i].setColorFilter(
                    i < r.rating ? 0xFFFFC107 : 0xFFCCCCCC
            );
        }
    }
    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivStar1, ivStar2, ivStar3, ivStar4, ivStar5;
        TextView tvReviewerName, tvReviewDate, tvReviewText;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvReviewerName = itemView.findViewById(R.id.tvReviewerName);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            tvReviewText = itemView.findViewById(R.id.tvReviewText);
            ivStar1 = itemView.findViewById(R.id.ivStar1);
            ivStar2 = itemView.findViewById(R.id.ivStar2);
            ivStar3 = itemView.findViewById(R.id.ivStar3);
            ivStar4 = itemView.findViewById(R.id.ivStar4);
            ivStar5 = itemView.findViewById(R.id.ivStar5);
        }
    }
}