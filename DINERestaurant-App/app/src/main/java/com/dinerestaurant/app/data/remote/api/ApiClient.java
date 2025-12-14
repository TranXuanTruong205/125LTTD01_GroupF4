package com.dinerestaurant.app.data.remote.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Android Emulator → localhost
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    /**
     * Khởi tạo ApiClient – GỌI 1 LẦN DUY NHẤT (Application)
     */
    public static void init(Context context) {
        if (retrofit != null) return;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context)) // 🔑 JWT
                .addInterceptor(logging)                      // 🪵 Log API
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // ====== Các API ======

    public static AuthApi getAuthApi() {
        return retrofit.create(AuthApi.class);
    }
    public static CartApi getCartApi() {
        return retrofit.create(CartApi.class);
    }
}
