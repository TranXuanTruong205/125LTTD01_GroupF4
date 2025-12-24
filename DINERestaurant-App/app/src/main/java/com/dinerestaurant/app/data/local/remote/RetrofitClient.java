package com.dinerestaurant.app.data.local.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Đổi BASE_URL cho đúng backend của m
    // Emulator: dùng 10.0.2.2 thay cho localhost
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
