package com.dinerestaurant.app.data.remote.api;

import android.content.Context;

import com.dinerestaurant.app.data.local.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(Context context) {
        this.tokenManager = new TokenManager(context.getApplicationContext());
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Nếu request đã có Authorization header thì không thêm nữa
        if (original.header("Authorization") != null) {
            return chain.proceed(original);
        }

        String token = tokenManager.getToken();
        if (token == null || token.isEmpty()) {
            // Không có token -> gửi request như bình thường
            return chain.proceed(original);
        }

        // Gắn header Authorization (dùng header() thay vì addHeader() để tránh
        // duplicate)
        Request newRequest = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(newRequest);
    }
}