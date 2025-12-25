package com.dinerestaurant.app.ui.home;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import java.util.List;
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<String> bannerImages;
    private Context context;
    public BannerAdapter(Context context, List<String> bannerImages) {
        this.context = context;
        this.bannerImages = bannerImages;
    }
    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imagePath = bannerImages.get(position);
        // --- GLIDE LOAD ẢNH ---
        Object imageSource;
        if (imagePath != null && !imagePath.startsWith("http")) {
            imageSource = "file:///android_asset/" + imagePath;
        } else {
            imageSource = imagePath;
        }
        Glide.with(context)
                .load(imageSource)
                .centerCrop()
                .into(holder.ivBanner);
    }
    @Override
    public int getItemCount() {
        return bannerImages.size();
    }
    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
        }
    }
}