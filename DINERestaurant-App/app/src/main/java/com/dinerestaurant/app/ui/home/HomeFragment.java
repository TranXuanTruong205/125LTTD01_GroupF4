package com.dinerestaurant.app.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.CategoryItem;
import com.dinerestaurant.app.model.SpecialOfferItem;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ApiService;
import com.dinerestaurant.app.data.remote.dto.CategoryDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutIndicator;
    private RecyclerView rvCategories;
    private RecyclerView rvSpecialOffers;
    private CategoryAdapter categoryAdapter;
    private SpecialOfferAdapter specialOfferAdapter;
    private ImageView btnCart;
    private ImageView btnChat;

    private ApiService apiService;
    private List<CategoryItem> categoryItems = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ view
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        layoutIndicator = view.findViewById(R.id.layoutIndicator);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvSpecialOffers = view.findViewById(R.id.rvSpecialOffers);
        btnCart = view.findViewById(R.id.ivCart);
        btnChat = view.findViewById(R.id.ivChat);

        apiService = ApiClient.getApiService();

        // Categories
        setupCategoriesRecycler();
        loadCategories();

        // Banner + special offers
        setupBanner();
        setupSpecialOffers(view);

        // Button
        setupCartButton(view);
        setupChatButton(view);

        return view;
    }

    // ========================= CATEGORIES =========================
    // Trong HomeFragment.java

    // 1. Sửa setupCategoriesRecycler: Xóa tham số assetManager
    private void setupCategoriesRecycler() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));

        categoryAdapter = new CategoryAdapter(
                categoryItems,
                // XÓA DÒNG NÀY: requireContext().getAssets(),
                item -> {
                    if ("More".equals(item.getName())) {
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_homeFragment_to_categoryFragment);
                    } else {
                        Bundle args = new Bundle();
                        args.putInt("categoryId", item.getId());
                        args.putString("categoryName", item.getName());

                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_homeFragment_to_categoryProductsFragment, args);
                    }
                }
        );

        rvCategories.setAdapter(categoryAdapter);
    }

    // 2. Sửa loadCategories: Lấy icon trực tiếp từ API (DB)
    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call,
                                   Response<List<CategoryDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    return;
                }

                List<CategoryItem> list = new ArrayList<>();

                // Giới hạn hiển thị 7 item đầu tiên cho Home, item thứ 8 là "More"
                int limit = 7;
                int count = 0;

                for (CategoryDto dto : response.body()) {
                    if (count >= limit) break; // Chỉ lấy 7 cái đầu

                    // Lấy trực tiếp icon từ DB (vì bạn đã update SQL rồi)
                    list.add(new CategoryItem(
                            dto.getCategoryId(),
                            dto.getCategoryName(),
                            dto.getIcon()
                    ));
                    count++;
                }

                // Luôn thêm item "More" ở cuối
                list.add(new CategoryItem(
                        -1,
                        "More",
                        "images/categories/ic_more.png"
                ));

                categoryAdapter.setItems(list);
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ========================= BANNER =========================
    private void setupBanner() {
        List<String> bannerList = new ArrayList<>();
        bannerList.add("images/home_banner/Banner.png");
        bannerList.add("images/home_banner/Banner 2.png");
        bannerList.add("images/home_banner/Banner 3.png");

        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), bannerList);
        viewPagerBanner.setAdapter(bannerAdapter);

        viewPagerBanner.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        updateIndicators(position);
                    }
                });
    }

    // ========================= SPECIAL OFFERS =========================
    private void setupSpecialOffers(View view) {
        rvSpecialOffers.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));

        List<SpecialOfferItem> offers = new ArrayList<>();
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger.png",
                "Chicken Burger", 4.9, 10.00, 6.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger-1.png",
                "Beef Burger", 4.9, 12.00, 10.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Ramen Noodles.png",
                "Ramen Noodles", 4.9, 22.00, 15.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Pho Noodles.png",
                "Pho Noodles", 4.9, 24.00, 20.00));

        specialOfferAdapter = new SpecialOfferAdapter(
                offers,
                requireContext().getAssets(),
                item -> {
                    try {
                        Navigation.findNavController(view)
                                .navigate(R.id.action_homeFragment_to_categoryProductsFragment);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        rvSpecialOffers.setAdapter(specialOfferAdapter);

        View viewAllBtn = view.findViewById(R.id.tvViewAll);
        if (viewAllBtn != null) {
            viewAllBtn.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(view)
                            .navigate(R.id.specialOffersFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // ========================= BUTTONS =========================
    private void setupCartButton(View view) {
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                try {
                    Navigation.findNavController(view)
                            .navigate(R.id.cartFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void setupChatButton(View view) {
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                try {
                    navController.navigate(
                            R.id.action_homeFragment_to_messageFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(),
                            "Lỗi: Không tìm thấy action trong Nav Graph.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // ========================= INDICATOR =========================
    private void updateIndicators(int position) {
        if (layoutIndicator == null) return;

        for (int i = 0; i < layoutIndicator.getChildCount(); i++) {
            View dot = layoutIndicator.getChildAt(i);
            if (dot == null) continue;

            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams) dot.getLayoutParams();

            if (i == position) {
                dot.setBackgroundColor(Color.parseColor("#FF6B35"));
                params.width = dpToPx(24);
            } else {
                dot.setBackgroundColor(Color.parseColor("#CCCCCC"));
                params.width = dpToPx(8);
            }
            dot.setLayoutParams(params);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}