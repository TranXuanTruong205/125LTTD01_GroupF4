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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Mặc dù MessageFragment được import, chúng ta sẽ không dùng nó trực tiếp ở đây
// mà dùng ID của nó trong Nav Graph.


import java.util.ArrayList;
import java.util.List;

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

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // 1. Inflate View
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 2. Ánh xạ Views
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        layoutIndicator = view.findViewById(R.id.layoutIndicator);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvSpecialOffers = view.findViewById(R.id.rvSpecialOffers);
        btnCart = view.findViewById(R.id.ivCart);
        btnChat = view.findViewById(R.id.ivChat);

        // 3. Setup Categories RecyclerView
        setupCategories();

        // 4. Setup Banner ViewPager
        setupBanner();

        // 5. Setup Special Offers RecyclerView
        setupSpecialOffers(view);

        // 6. BỔ SUNG SỰ KIỆN CLICK CHO btnCart
        setupCartButton(view);

        // 7. BỔ SUNG SỰ KIỆN CLICK CHO btnChat
        setupChatButton(view); // <-- Cần truyền view để lấy NavController

        return view;
    }

    private void setupCategories() {
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4));

        // Gọi API lấy categories
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<CategoryDto> dtoList = response.body();
                List<CategoryItem> categories = new ArrayList<>();

                int index = 0;
                for (CategoryDto dto : dtoList) {
                    // TẠM THỜI: map icon → asset theo index hoặc để null
                    String imagePath = null;
                    // Nếu bạn về sau lưu icon giống tên file assets, có thể làm:
                    // imagePath = "images/categories/" + dto.getIcon();

                    categories.add(new CategoryItem(
                            dto.getCategoryId(),
                            imagePath,
                            dto.getCategoryName()
                    ));
                    index++;
                }

                categoryAdapter = new CategoryAdapter(
                        categories,
                        requireContext().getAssets(),
                        item -> {
                            if (item.getName().equals("More")) {
                                // Nếu vẫn muốn giữ logic More thì xử lý riêng,
                                // còn hiện tại data từ BE thường không có "More"
                                try {
                                    Navigation.findNavController(requireView())
                                            .navigate(R.id.action_homeFragment_to_categoryFragment);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // Chuyển sang CategoryProductsFragment kèm categoryId + name
                                Bundle bundle = new Bundle();
                                bundle.putInt("categoryId", item.getId());
                                bundle.putString("categoryName", item.getName());
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_homeFragment_to_categoryProductsFragment, bundle);
                            }
                        }
                );
                rvCategories.setAdapter(categoryAdapter);
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    private void setupBanner() {
        List<String> bannerList = new ArrayList<>();
        bannerList.add("images/home_banner/Banner.png");
        bannerList.add("images/home_banner/Banner 2.png");
        bannerList.add("images/home_banner/Banner 3.png");

        BannerAdapter bannerAdapter = new BannerAdapter(getContext(), bannerList);
        viewPagerBanner.setAdapter(bannerAdapter);

        // Sửa lỗi: Dùng onPageSelected thay vì onPageScrolled
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicators(position);
            }
        });
    }

    private void setupSpecialOffers(View view) {
        rvSpecialOffers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<SpecialOfferItem> offers = new ArrayList<>();
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger.png", "Chicken Burger", 4.9, 10.00, 6.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Burger-1.png", "Beef Burger", 4.9, 12.00, 10.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Ramen Noodles.png", "Ramen Noodles", 4.9, 22.00, 15.00));
        offers.add(new SpecialOfferItem("images/special_offers/Image Pho Noodles.png", "Pho Noodles", 4.9, 24.00, 20.00));

        specialOfferAdapter = new SpecialOfferAdapter(offers, requireContext().getAssets(), item -> {
            // Xử lý khi click vào món Special Offer
            try {
                // Ví dụ: Chuyển sang màn hình chi tiết sản phẩm
                // Bundle bundle = new Bundle();
                // bundle.putString("product_name", item.getName());

                androidx.navigation.Navigation.findNavController(view)
                        .navigate(R.id.action_homeFragment_to_categoryProductsFragment); // Đảm bảo ID này đúng trong nav_graph
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        rvSpecialOffers.setAdapter(specialOfferAdapter);

        // Xử lý nút View All
        View viewAllBtn = view.findViewById(R.id.tvViewAll);
        if (viewAllBtn != null) {
            viewAllBtn.setOnClickListener(v -> {
                // Đảm bảo bạn đã có Navigation Graph và ID này
                try {
                    androidx.navigation.Navigation.findNavController(view).navigate(R.id.specialOffersFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // HÀM SETUP CART BUTTON
    private void setupCartButton(View view) {
        if (btnCart != null) {
            btnCart.setOnClickListener(v -> {
                try {
                    // SỬ DỤNG Navigation Component để chuyển đến CartFragment
                    androidx.navigation.Navigation.findNavController(view).navigate(R.id.cartFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    // 🚀 HÀM SETUP CHAT BUTTON ĐÃ SỬA LẠI (Sử dụng Navigation Component)
    private void setupChatButton(View view) {
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(v);
                try {
                    // SỬ DỤNG ID ACTION THỰC TẾ TRONG NAV GRAPH CỦA BẠN
                    navController.navigate(R.id.action_homeFragment_to_messageFragment);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(requireContext(), "Lỗi: Không tìm thấy action Promotions trong Nav Graph.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    // --- CÁC HÀM HỖ TRỢ UI ---

    // Hàm cập nhật màu sắc/kích thước Indicator
    private void updateIndicators(int position) {
        if (layoutIndicator == null) return;

        // Duyệt qua 3 view con trong layoutIndicator
        for (int i = 0; i < layoutIndicator.getChildCount(); i++) {
            View dot = layoutIndicator.getChildAt(i);
            if (dot == null) continue;

            if (i == position) {
                // Item đang chọn: Màu cam, rộng hơn
                dot.setBackgroundColor(Color.parseColor("#FF6B35"));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
                params.width = dpToPx(24); // Rộng 24dp
                dot.setLayoutParams(params);
            } else {
                // Item khác: Màu xám, ngắn hơn
                dot.setBackgroundColor(Color.parseColor("#CCCCCC"));
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dot.getLayoutParams();
                params.width = dpToPx(8); // Rộng 8dp
                dot.setLayoutParams(params);
            }
        }
    }

    // Hàm phụ để đổi dp sang pixel
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}