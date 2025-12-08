package com.dinerestaurant.app.ui.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> bannerImages; // Danh sách đường dẫn ảnh trong assets
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

        // Load ảnh từ assets
        try {
            InputStream stream = context.getAssets().open(imagePath);
            Drawable drawable = Drawable.createFromStream(stream, null);
            holder.ivBanner.setImageDrawable(drawable);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
