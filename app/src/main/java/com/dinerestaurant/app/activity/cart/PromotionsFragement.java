package com.dinerestaurant.app.activity.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dinerestaurant.app.R;

public class PromotionsFragement extends Fragment {

    public PromotionsFragement() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_promotions, container, false);
    }
}