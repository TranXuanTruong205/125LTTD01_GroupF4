package com.dinerestaurant.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.ReviewItem;

import java.util.ArrayList;
import java.util.List;

public class ReviewListFragment extends Fragment {

    private RecyclerView rvReviews;
    private ReviewAdapter adapter;

    public ReviewListFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);

        // Setup RecyclerView
        rvReviews = view.findViewById(R.id.rvReviews);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create reviews data với avatar từ assets
        List<ReviewItem> items = new ArrayList<>();
        items.add(new ReviewItem("images/avatar_review/Rectangle.png", 
            "John Doe", "29/03/2024", 5, 
            "Delicious chicken burger! Loved the crispy chicken and the bun was perfectly toasted. Definitely a new favorite!"));
        
        items.add(new ReviewItem("images/avatar_review/Rectangle-1.png", 
            "David", "10/04/2024", 5, 
            "Absolutely delicious! The chicken burger was juicy and flavorful, with just the right amount of seasoning. Highly recommend!"));
        
        items.add(new ReviewItem("images/avatar_review/Rectangle-2.png", 
            "Tom", "05/04/2024", 5, 
            "One of the best chicken burgers I've ever had! The chicken was tender and the bun was soft. Loved every bite!"));
        
        items.add(new ReviewItem("images/avatar_review/Rectangle-3.png", 
            "Adam", "25/03/2024", 5, 
            "The chicken burger was decent, but I felt like it could use more seasoning. Overall, it was tasty and satisfying."));
        
        items.add(new ReviewItem("images/avatar_review/Rectangle-4.png", 
            "James", "29/03/2024", 4, 
            "The chicken burger was okay, but it was a bit overcooked for my liking. The toppings were fresh, though."));

        // Setup adapter với AssetManager
        adapter = new ReviewAdapter(items, requireContext().getAssets());
        rvReviews.setAdapter(adapter);

        // Back button
        view.findViewById(R.id.ivBack).setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
