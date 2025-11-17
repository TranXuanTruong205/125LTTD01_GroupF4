package com.dinerestaurant.app.activity.home;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<ReviewItem> items;
    private AssetManager assetManager;

    public ReviewAdapter(List<ReviewItem> items, AssetManager assetManager) {
        this.items = items;
        this.assetManager = assetManager;
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
        ReviewItem item = items.get(position);
        holder.tvReviewerName.setText(item.getReviewerName());
        holder.tvReviewDate.setText(item.getReviewDate());
        holder.tvReviewText.setText(item.getReviewText());
        
        // Set rating stars
        ImageView[] stars = {holder.ivStar1, holder.ivStar2, holder.ivStar3, holder.ivStar4, holder.ivStar5};
        for (int i = 0; i < stars.length; i++) {
            if (i < item.getRating()) {
                stars[i].setColorFilter(0xFFFFC107); // Yellow
            } else {
                stars[i].setColorFilter(0xFFCCCCCC); // Gray
            }
        }
        
        // Load avatar từ assets
        try {
            InputStream is = assetManager.open(item.getAvatarPath());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.ivAvatar.setImageBitmap(bitmap);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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
