package com.dinerestaurant.app.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dinerestaurant.app.ui.home.ReviewApiAdapter;
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

    private RecyclerView rvReviews;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_review_list, container, false);

        // 1. Ánh xạ RecyclerView
        rvReviews = view.findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Nhận itemId từ ProductDetail
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

                if (response.isSuccessful() && response.body() != null) {
                    rvReviews.setAdapter(
                            new ReviewApiAdapter(response.body())
                    );
                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
