package com.dinerestaurant.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.UserApi;
import com.dinerestaurant.app.model.User;
import com.dinerestaurant.app.ui.auth.ProfileSetupActivity;
import com.dinerestaurant.app.ui.other.LikedFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.dinerestaurant.app.data.local.TokenManager;
import com.dinerestaurant.app.data.local.TableSessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.dinerestaurant.app.ui.auth.LoginActivity;

public class ProfileFragment extends Fragment {
    private ImageView imgAvatar;
    private TextView tvFullName, tvPhone, tvEmail;
    private ImageButton editProfileButton, btnBack;
    private LinearLayout btnMessages, btnLogout;

    private UserApi userApi;

    private static final int CONTAINER_ID = R.id.nav_host_fragment;
    private static final String BACK_STACK_TAG = "PROFILE_SUB_SCREEN";

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Bind views
        btnLogout = view.findViewById(R.id.btnLogout);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);

        editProfileButton = view.findViewById(R.id.editProfileBtn);
        btnBack = view.findViewById(R.id.btnBack);
        btnMessages = view.findViewById(R.id.btnmessages);

        LinearLayout llMyLocations = view.findViewById(R.id.llMyLocations);
        LinearLayout llLiked = view.findViewById(R.id.llLiked);
        LinearLayout llOrders = view.findViewById(R.id.llOrders);

        // Init API
        userApi = ApiClient.getUserApi();

        // Load profile
        loadProfile();

        // ===== Listeners =====
        btnLogout.setOnClickListener(v -> logout());

        editProfileButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), ProfileSetupActivity.class)));

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        btnMessages.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_profileFragment_to_messageFragment));

        llMyLocations.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_profileFragment_to_myLocationsFragment));

        llLiked.setOnClickListener(v -> replaceFragment(new LikedFragment()));

        // Orders click
        llOrders.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_profileFragment_to_orderFragment));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        userApi.getProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindUser(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
            }
        });
    }

    private void logout() {

        // 1. Xoá token JWT
        new TokenManager(requireContext()).clear();

        // 2. Xoá table session (đặt bàn đã quét QR)
        TableSessionManager.getInstance(requireContext()).clearAll();

        // 2. Logout Google (nếu user login Google)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        googleSignInClient.signOut().addOnCompleteListener(task -> {

            // 3. Quay về LoginActivity
            Intent intent = new Intent(requireContext(), LoginActivity.class);

            // Xoá toàn bộ back stack
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void bindUser(User user) {

        tvFullName.setText(user.getFullName());
        tvPhone.setText("+" + user.getPhoneNumber());
        tvEmail.setText(user.getEmail());

        String avatarUrl = user.getProfilePicture();

        Log.d("PROFILE", "Avatar URL = " + avatarUrl);

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePicture()) // URL từ DB
                    .placeholder(R.drawable.pic_avatar_default)
                    .error(R.drawable.pic_avatar_default)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.pic_avatar_default);
        }
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fm = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(CONTAINER_ID, newFragment);
        transaction.addToBackStack(BACK_STACK_TAG);
        transaction.commit();
    }
}
