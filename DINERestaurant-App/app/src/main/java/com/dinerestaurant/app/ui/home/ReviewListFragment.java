package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ReviewApi;
import com.dinerestaurant.app.data.remote.dto.ReviewResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewListFragment extends Fragment {

    private TextView tvAvgRating, tvTotalReviews;
    private RatingBar ratingBarAvg;
    private ProgressBar pb5, pb4, pb3, pb2, pb1;
    private RecyclerView rvReviews;
    private ImageView ivBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review_list, container, false);

        // ===== ÁNH XẠ =====
        ivBack = view.findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        tvAvgRating = view.findViewById(R.id.tvAverageRating);
        tvTotalReviews = view.findViewById(R.id.tvTotalReviews);
        ratingBarAvg = view.findViewById(R.id.ratingBarAvg);

        pb5 = view.findViewById(R.id.pb5);
        pb4 = view.findViewById(R.id.pb4);
        pb3 = view.findViewById(R.id.pb3);
        pb2 = view.findViewById(R.id.pb2);
        pb1 = view.findViewById(R.id.pb1);

        rvReviews = view.findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // ===== LẤY itemId =====
        int itemId = getArguments() != null
                ? getArguments().getInt("itemId", -1)
                : -1;

        if (itemId != -1) {
            loadReviews(itemId);
        }

        return view;
    }

    private void loadReviews(int itemId) {
        ReviewApi api = ApiClient.getReviewApi();

        api.getReviewsByItem(itemId).enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call,
                                   Response<List<ReviewResponse>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                List<ReviewResponse> reviews = response.body();
                rvReviews.setAdapter(new ReviewApiAdapter(reviews));

                int total = reviews.size();
                if (total == 0) {
                    tvAvgRating.setText("0.0");
                    tvTotalReviews.setText("(0)");
                    ratingBarAvg.setRating(0);
                    return;
                }

                int sum = 0, c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0;

                for (ReviewResponse r : reviews) {
                    sum += r.rating;
                    switch (r.rating) {
                        case 5: c5++; break;
                        case 4: c4++; break;
                        case 3: c3++; break;
                        case 2: c2++; break;
                        case 1: c1++; break;
                    }
                }

                float avg = (float) sum / total;

                // ===== SET UI =====
                tvAvgRating.setText(String.format("%.1f", avg));
                tvTotalReviews.setText("(" + total + ")");
                ratingBarAvg.setRating(avg);

                pb5.setProgress(c5 * 100 / total);
                pb4.setProgress(c4 * 100 / total);
                pb3.setProgress(c3 * 100 / total);
                pb2.setProgress(c2 * 100 / total);
                pb1.setProgress(c1 * 100 / total);
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
